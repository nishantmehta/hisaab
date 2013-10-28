package com.hisaab.ui;

public class TransactionFlagValues {

// next available flag is -10	
// -99 is reserved for settled transaction
	
//general status	
public static int activeTransaction=0;
public static int deleteApproval=-5;
public static int settleApproval=-8 ;
public static int settledTransaction=-99;
public static int denyTransaction=-9;

//on sender status
public static int transactionApprovalRequest=-1;
public static int deleteRequest=-3;
public static int settleRequest=-6 ;

//on receiver status
public static int transactionApprovalRequestOnReceiver=-2;
public static int deleteRequestOnReceiver=-4;
public static int settleRequestOnReceiver=-7 ;









}
