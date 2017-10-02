/**
 * 
 */
package org.frontuari.events;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import org.adempiere.base.event.AbstractEventHandler;
import org.adempiere.base.event.IEventTopics;
import org.compiere.model.MBPartner;
import org.compiere.model.MDocType;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.PO;
import org.compiere.model.Tax;
import org.compiere.model.X_C_POSPayment;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.osgi.service.event.Event;

/**
 * @author jcolmenarez,1 oct. 2017
 *
 */
public class LVE_TaxAdjustmentEvents extends AbstractEventHandler {
	
	CLogger log = CLogger.getCLogger(LVE_TaxAdjustmentEvents.class);
	
	protected void initialize() {
		//	Listener
		registerTableEvent(IEventTopics.PO_AFTER_NEW, MOrderLine.Table_Name);
		registerTableEvent(IEventTopics.PO_AFTER_CHANGE, MOrderLine.Table_Name);
		registerTableEvent(IEventTopics.PO_AFTER_DELETE, MOrderLine.Table_Name);
		registerTableEvent(IEventTopics.PO_AFTER_NEW, MInvoiceLine.Table_Name);
		registerTableEvent(IEventTopics.PO_AFTER_CHANGE, MInvoiceLine.Table_Name);
		registerTableEvent(IEventTopics.PO_AFTER_DELETE, MInvoiceLine.Table_Name);
		registerTableEvent(IEventTopics.PO_AFTER_NEW, X_C_POSPayment.Table_Name);
		registerTableEvent(IEventTopics.PO_AFTER_CHANGE, X_C_POSPayment.Table_Name);
		registerTableEvent(IEventTopics.PO_AFTER_DELETE, X_C_POSPayment.Table_Name);
		
	}
	
	protected void doHandleEvent(Event event) {
		PO po = getPO(event);
		String type = event.getTopic();
		int C_Tax_ID;
		if(po instanceof MOrderLine){
			MOrderLine ol = (MOrderLine)po;
			if(ol.getC_Tax().getRate().compareTo(new BigDecimal(0)) != 0){
				MOrder o = (MOrder) ol.getC_Order();
				MBPartner bp = (MBPartner) o.getC_BPartner();
				if(o.isSOTrx()){
					if(type.equalsIgnoreCase(IEventTopics.PO_AFTER_NEW)
							|| type.equalsIgnoreCase(IEventTopics.PO_AFTER_CHANGE)){
						//	Get Tax ID
						C_Tax_ID = getTax(o.get_ValueAsBoolean("IsElectronicPayment"), o.getDateOrdered(), 
								bp.get_ValueAsInt("LCO_TaxIdType_ID"), o.getTotalLines());
						//	Check if exists Tax Changed
						if(C_Tax_ID != -1){
							//	Check if Tax ID is distinct 
							if(ol.getC_Tax_ID()!=C_Tax_ID){
								//	Set new Tax
								ol.setC_Tax_ID(C_Tax_ID);
								ol.saveEx();
								//	Search all lines with distinct Tax ID
								String sql = "SELECT ol.C_OrderLine_ID FROM C_OrderLine ol "
										+ "INNER JOIN C_Tax t ON ol.C_Tax_ID = t.C_Tax_ID AND t.Rate <> 0 "
										+ "WHERE ol.C_Order_ID = ? AND ol.C_Tax_ID <> ? "
										+ "ORDER BY ol.C_OrderLine_ID ASC";
								PreparedStatement ps = null;
								ResultSet rs = null;
								try{
									ps = DB.prepareStatement(sql.toString(),null);
									ps.setInt(1, ol.getC_Order_ID());
									ps.setInt(2, C_Tax_ID);
									rs = ps.executeQuery();
									while (rs.next())
									{
										MOrderLine otherLine = new MOrderLine(po.getCtx(), rs.getInt("C_OrderLine_ID"), po.get_TrxName());
										otherLine.setC_Tax_ID(C_Tax_ID);
										otherLine.saveEx();
									}
								}catch(Exception e){
									log.severe(e.getMessage());
								} finally {
									DB.close(rs, ps);
									rs = null; ps = null;
								}
							}
						}
					}
					else if(type.equalsIgnoreCase(IEventTopics.PO_AFTER_DELETE)){
						//	Get Tax ID
						C_Tax_ID = getTax(o.get_ValueAsBoolean("IsElectronicPayment"), o.getDateOrdered(), 
								bp.get_ValueAsInt("LCO_TaxIdType_ID"), o.getTotalLines());
						//	Check if exists Tax Changed
						if(C_Tax_ID != -1){
							MOrderLine[] oLines = o.getLines();
							for(MOrderLine line : oLines){
								if(line.getC_Tax().getRate().compareTo(new BigDecimal(0)) != 0){
									line.setC_Tax_ID(C_Tax_ID);
									line.saveEx();	
								}
							}
						}
					}
				}
			}
		}

		if(po instanceof MInvoiceLine){
			MInvoiceLine il = (MInvoiceLine)po;
			if(il.getC_Tax().getRate().compareTo(new BigDecimal(0)) != 0){
				MInvoice i = (MInvoice) il.getC_Invoice();
				MBPartner bp = (MBPartner) i.getC_BPartner();
				if(i.isSOTrx() && i.getC_Order_ID()==0 && !i.getC_DocTypeTarget().getDocBaseType().equals(MDocType.DOCBASETYPE_ARCreditMemo)){
					if(type.equalsIgnoreCase(IEventTopics.PO_AFTER_NEW)
							|| type.equalsIgnoreCase(IEventTopics.PO_AFTER_CHANGE)){
						//	Get Tax ID
						C_Tax_ID = getTax(i.get_ValueAsBoolean("IsElectronicPayment"), i.getDateInvoiced(), 
								bp.get_ValueAsInt("LCO_TaxIdType_ID"), i.getTotalLines());
						//	Check if exists Tax Changed
						if(C_Tax_ID != -1){
							//	Check if Tax ID is distinct 
							if(il.getC_Tax_ID()!=C_Tax_ID){
								//	Set new Tax
								il.setC_Tax_ID(C_Tax_ID);
								il.saveEx();
								//	Search all lines with distinct Tax ID
								String sql = "SELECT il.C_InvoiceLine_ID FROM C_InvoiceLine il "
										+ "INNER JOIN C_Tax t ON il.C_Tax_ID = t.C_Tax_ID AND t.Rate <> 0 "
										+ "WHERE il.C_Invoice_ID = ? AND il.C_Tax_ID <> ? "
										+ "ORDER BY il.C_InvoiceLine_ID ASC";
								PreparedStatement ps = null;
								ResultSet rs = null;
								try{
									ps = DB.prepareStatement(sql.toString(),null);
									ps.setInt(1, il.getC_Invoice_ID());
									ps.setInt(2, C_Tax_ID);
									rs = ps.executeQuery();
									while (rs.next())
									{
										MInvoiceLine otherLine = new MInvoiceLine(po.getCtx(), rs.getInt("C_InvoiceLine_ID"), po.get_TrxName());
										otherLine.setC_Tax_ID(C_Tax_ID);
										otherLine.saveEx();
									}
								}catch(Exception e){
									log.severe(e.getMessage());
								} finally {
									DB.close(rs, ps);
									rs = null; ps = null;
								}
							}
						}
						
					}
					else if(type.equalsIgnoreCase(IEventTopics.PO_AFTER_DELETE)){
						//	Get Tax ID
						C_Tax_ID = getTax(i.get_ValueAsBoolean("IsElectronicPayment"), i.getDateInvoiced(), 
								bp.get_ValueAsInt("LCO_TaxIdType_ID"), i.getTotalLines());
						//	Check if exists Tax Changed
						if(C_Tax_ID != -1){
							MInvoiceLine[] iLines = i.getLines();
							for(MInvoiceLine line : iLines){
								if(line.getC_Tax().getRate().compareTo(new BigDecimal(0)) != 0){
									line.setC_Tax_ID(C_Tax_ID);
									line.saveEx();	
								}
							}
						}
					}
				}
			}
		}

		if(po instanceof X_C_POSPayment){
			X_C_POSPayment pp = (X_C_POSPayment)po;
			MOrder o = (MOrder)pp.getC_Order();
			if(type.equalsIgnoreCase(IEventTopics.PO_AFTER_NEW)
					|| type.equalsIgnoreCase(IEventTopics.PO_AFTER_CHANGE)){
				if(!pp.get_ValueAsBoolean("IsElectronicPayment")){
					o.set_ValueOfColumn("IsElectronicPayment", false);
					o.saveEx();
					MOrderLine[] oLines = o.getLines();
					for(MOrderLine line : oLines){
						if(line.getC_Tax().getRate().compareTo(new BigDecimal(0)) != 0){
							C_Tax_ID = Tax.get(po.getCtx(),line.getM_Product_ID(),line.getC_Charge_ID(), 
									o.getDateOrdered(), o.getDatePromised(),line.getAD_Org_ID(), o.getM_Warehouse_ID(), 
									o.getBill_Location_ID(), o.getC_BPartner_Location_ID(), o.isSOTrx());
							line.setC_Tax_ID(C_Tax_ID);
							line.saveEx();
						}
					}
				}
				else{
					if(!o.get_ValueAsBoolean("IsElectronicPayment")){
						o.set_ValueOfColumn("IsElectronicPayment", true);
						o.saveEx();
						MBPartner bp = (MBPartner) o.getC_BPartner();
						//	Get Tax ID
						C_Tax_ID = getTax(o.get_ValueAsBoolean("IsElectronicPayment"), o.getDateOrdered(), 
								bp.get_ValueAsInt("LCO_TaxIdType_ID"), o.getTotalLines());
						//	Check if exists Tax Changed
						if(C_Tax_ID != -1){
							MOrderLine[] oLines = o.getLines();
							for(MOrderLine line : oLines){
								if(line.getC_Tax().getRate().compareTo(new BigDecimal(0)) != 0){
									line.setC_Tax_ID(C_Tax_ID);
									line.saveEx();
								}
							}
						}
					}
				}
				
			}
			else if(type.equalsIgnoreCase(IEventTopics.PO_AFTER_DELETE)){
				String sql ="SELECT COUNT(*) FROM C_POSPayment WHERE IsElectronicPayment = 'N' AND C_Order_ID = ? ";
				int Count = DB.getSQLValue(po.get_TrxName(), sql, o.getC_Order_ID());
				if(Count==0){
					o.set_ValueOfColumn("IsElectronicPayment", true);
					o.saveEx();
					MBPartner bp = (MBPartner) o.getC_BPartner();
					//	Get Tax ID
					C_Tax_ID = getTax(o.get_ValueAsBoolean("IsElectronicPayment"), o.getDateOrdered(), 
							bp.get_ValueAsInt("LCO_TaxIdType_ID"), o.getTotalLines());
					//	Check if exists Tax Changed
					if(C_Tax_ID != -1){
						MOrderLine[] oLines = o.getLines();
						for(MOrderLine line : oLines){
							line.setC_Tax_ID(C_Tax_ID);
							line.saveEx();
						}
					}
				}
			}
		}
	}
	
	public int getTax(boolean IsElectronicPayment, Timestamp DateTrx, int TaxIdType, BigDecimal Amt){
		String sql = "SELECT tml.C_Tax_ID FROM LVE_TaxAdjustment tml "
				+ "LEFT JOIN LVE_TaxAdjustmentRule tmlr ON tml.LVE_TaxAdjustment_ID = tmlr.LVE_TaxAdjustment_ID "
				+ "WHERE tml.IsElectronicPayment = ? "
				+ "AND ? BETWEEN tml.ValidFrom AND tml.ValidTo "
				+ "AND (tmlr.LCO_TaxIdType_ID = ? OR tmlr.LCO_TaxIdType_ID IS NULL)"
				+ "AND (CASE WHEN COALESCE(tmlr.MinAmt,0) = 0 AND COALESCE(tmlr.MaxAmt,0) = 0 THEN True "
				+ "WHEN COALESCE(tmlr.MinAmt,0) <> 0 AND COALESCE(tmlr.MaxAmt,0) = 0 THEN ? >= tmlr.MinAmt "
				+ "WHEN COALESCE(tmlr.MinAmt,0) = 0 AND COALESCE(tmlr.MaxAmt,0) <> 0 THEN ? <= tmlr.MaxAmt "
				+ "WHEN COALESCE(tmlr.MinAmt,0) <> 0 AND COALESCE(tmlr.MaxAmt,0) <> 0 THEN ? BETWEEN tmlr.MinAmt AND tmlr.MaxAmt END) "
				+ "ORDER BY COALESCE(tmlr.MinAmt,0),COALESCE(tmlr.MaxAmt,0) ASC";
		
		int C_Tax_ID = DB.getSQLValue(null, sql, (IsElectronicPayment==true ? "Y" : "N"),DateTrx,TaxIdType,Amt,Amt,Amt);
			
		return C_Tax_ID;
	}

}
