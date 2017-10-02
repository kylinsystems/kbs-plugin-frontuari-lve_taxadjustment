/******************************************************************************
 * Product: iDempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2012 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY, without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program, if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
/** Generated Model - DO NOT CHANGE */
package org.frontuari.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.*;
import org.compiere.util.Env;

/** Generated Model for LVE_TaxAdjustmentRule
 *  @author iDempiere (generated) 
 *  @version Release 4.1 - $Id$ */
public class X_LVE_TaxAdjustmentRule extends PO implements I_LVE_TaxAdjustmentRule, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20171002L;

    /** Standard Constructor */
    public X_LVE_TaxAdjustmentRule (Properties ctx, int LVE_TaxAdjustmentRule_ID, String trxName)
    {
      super (ctx, LVE_TaxAdjustmentRule_ID, trxName);
      /** if (LVE_TaxAdjustmentRule_ID == 0)
        {
			setLCO_TaxIdType_ID (0);
			setLVE_TaxAdjustment_ID (0);
			setMaxAmt (Env.ZERO);
// 0
			setMinAmt (Env.ZERO);
// 0
        } */
    }

    /** Load Constructor */
    public X_LVE_TaxAdjustmentRule (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 3 - Client - Org 
      */
    protected int get_AccessLevel()
    {
      return accessLevel.intValue();
    }

    /** Load Meta Data */
    protected POInfo initPO (Properties ctx)
    {
      POInfo poi = POInfo.getPOInfo (ctx, Table_ID, get_TrxName());
      return poi;
    }

    public String toString()
    {
      StringBuffer sb = new StringBuffer ("X_LVE_TaxAdjustmentRule[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set TaxID Type.
		@param LCO_TaxIdType_ID TaxID Type	  */
	public void setLCO_TaxIdType_ID (int LCO_TaxIdType_ID)
	{
		if (LCO_TaxIdType_ID < 1) 
			set_Value (COLUMNNAME_LCO_TaxIdType_ID, null);
		else 
			set_Value (COLUMNNAME_LCO_TaxIdType_ID, Integer.valueOf(LCO_TaxIdType_ID));
	}

	/** Get TaxID Type.
		@return TaxID Type	  */
	public int getLCO_TaxIdType_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_LCO_TaxIdType_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.frontuari.model.I_LVE_TaxAdjustment getLVE_TaxAdjustment() throws RuntimeException
    {
		return (org.frontuari.model.I_LVE_TaxAdjustment)MTable.get(getCtx(), org.frontuari.model.I_LVE_TaxAdjustment.Table_Name)
			.getPO(getLVE_TaxAdjustment_ID(), get_TrxName());	}

	/** Set Tax Adjustment.
		@param LVE_TaxAdjustment_ID Tax Adjustment	  */
	public void setLVE_TaxAdjustment_ID (int LVE_TaxAdjustment_ID)
	{
		if (LVE_TaxAdjustment_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_LVE_TaxAdjustment_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_LVE_TaxAdjustment_ID, Integer.valueOf(LVE_TaxAdjustment_ID));
	}

	/** Get Tax Adjustment.
		@return Tax Adjustment	  */
	public int getLVE_TaxAdjustment_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_LVE_TaxAdjustment_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Max Amount.
		@param MaxAmt 
		Maximum Amount in invoice currency
	  */
	public void setMaxAmt (BigDecimal MaxAmt)
	{
		set_Value (COLUMNNAME_MaxAmt, MaxAmt);
	}

	/** Get Max Amount.
		@return Maximum Amount in invoice currency
	  */
	public BigDecimal getMaxAmt () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_MaxAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Min Amount.
		@param MinAmt 
		Minimum Amount in invoice currency
	  */
	public void setMinAmt (BigDecimal MinAmt)
	{
		set_Value (COLUMNNAME_MinAmt, MinAmt);
	}

	/** Get Min Amount.
		@return Minimum Amount in invoice currency
	  */
	public BigDecimal getMinAmt () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_MinAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}
}