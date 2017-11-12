package com.zkteco.biometric;

public class FingerprintSensorErrorCode {
  public static int ERROR_BEGIN = 64536;
  public static int ERROR_SUCCESS = 0;
  public static int ERROR_FAIL = ERROR_BEGIN + -1;
  public static int ERROR_OPEN_FAIL = ERROR_BEGIN + -2;
  public static int ERROR_NOT_OPENED = ERROR_BEGIN + -3;
  
  public static int ZKFP_ERR_ALREADY_INIT = 1;
  public static int ZKFP_ERR_OK = 0;
  public static int ZKFP_ERR_INITLIB = -1;
  public static int ZKFP_ERR_INIT = -2;
  public static int ZKFP_ERR_NO_DEVICE = -3;
  public static int ZKFP_ERR_NOT_SUPPORT = -4;
  public static int ZKFP_ERR_INVALID_PARAM = -5;
  public static int ZKFP_ERR_OPEN = -6;
  public static int ZKFP_ERR_INVALID_HANDLE = -7;
  public static int ZKFP_ERR_CAPTURE = -8;
  public static int ZKFP_ERR_EXTRACT_FP = -9;
  public static int ZKFP_ERR_ABSORT = -10;
  public static int ZKFP_ERR_MEMORY_NOT_ENOUGH = -11;
  public static int ZKFP_ERR_BUSY = -12;
  public static int ZKFP_ERR_ADD_FINGER = -13;
  public static int ZKFP_ERR_DEL_FINGER = -14;
  public static int ZKFP_ERR_FAIL = -17;
  public static int ZKFP_ERR_CANCEL = -18;
  public static int ZKFP_ERR_VERIFY_FP = -20;
  public static int ZKFP_ERR_MERGE = -22;
  public static int ZKFP_ERR_NOT_OPENED = -23;
  public static int ZKFP_ERR_NOT_INIT = -24;
  public static int ZKFP_ERR_ALREADY_OPENED = -25;
  public static int ZKFP_ERR_LOADIMAGE = -26;
  public static int ZKFP_ERR_ANALYSE_IMG = -27;
  public static int ZKFP_ERR_TIMEOUT = -28;
  
  public FingerprintSensorErrorCode() {}
}
