package com.zkteco.biometric;

public class FingerprintSensorEx {
  public FingerprintSensorEx() {}
  
  public static int Init() { return ZKFPService.Initialize(); }
  
  public static int Terminate()
  {
    return ZKFPService.Finalize();
  }
  
  public static int GetDeviceCount()
  {
    return ZKFPService.GetDeviceCount();
  }
  
  public static long OpenDevice(int index)
  {
    return ZKFPService.OpenDevice(index);
  }
  
  public static int CloseDevice(long devHandle)
  {
    return ZKFPService.CloseDevice(devHandle);
  }
  

  public static int SetParameters(long devHandle, int code, byte[] pramValue, int size)
  {
    return ZKFPService.SetParameter(devHandle, code, pramValue, size);
  }
  
  public static int GetParameters(long devHandle, int code, byte[] paramValue, int[] size)
  {
    return ZKFPService.GetParameter(devHandle, code, paramValue, size);
  }
  
  public static int AcquireFingerprint(long devHandle, byte[] imgBuffer, byte[] template, int[] size)
  {
    return ZKFPService.AcquireTemplate(devHandle, imgBuffer, template, size);
  }
  
  public static int AcquireFingerprintImage(long devHandle, byte[] imgbuf)
  {
    return ZKFPService.AcquireImage(devHandle, imgbuf);
  }
  
  public static long DBInit()
  {
    return ZKFPService.DBInit();
  }
  
  public static int DBFree(long dbHandle)
  {
    return ZKFPService.DBFree(dbHandle);
  }
  

  public static int DBSetParameter(long dbHandle, int code, int value)
  {
    return ZKFPService.DBSetParameter(dbHandle, code, value);
  }
  

  public static int DBGetParameter(long dbHandle, int code, int[] value)
  {
    return ZKFPService.DBGetParameter(dbHandle, code, value);
  }
  

  public static int DBMerge(long dbHandle, byte[] temp1, byte[] temp2, byte[] temp3, byte[] regTemp, int[] regTempLen)
  {
    if (0L == dbHandle)
    {
      return FingerprintSensorErrorCode.ZKFP_ERR_INVALID_HANDLE;
    }
    return ZKFPService.GenRegFPTemplate(temp1, temp2, temp3, regTemp, regTempLen);
  }
  

  public static int DBAdd(long dbHandle, int fid, byte[] regTemp)
  {
    if (0L == dbHandle)
    {
      return FingerprintSensorErrorCode.ZKFP_ERR_INVALID_HANDLE;
    }
    return ZKFPService.DBAdd(fid, regTemp);
  }
  

  public static int DBDel(long dbHandle, int fid)
  {
	 
    if (0L == dbHandle)
    {	
    	 System.out.println(dbHandle);
      return FingerprintSensorErrorCode.ZKFP_ERR_INVALID_HANDLE;
    }
    return ZKFPService.DBDel(fid);
  }
  

  public static int DBClear(long dbHandle)
  {
    if (0L == dbHandle)
    {
      return FingerprintSensorErrorCode.ZKFP_ERR_INVALID_HANDLE;
    }
    return ZKFPService.DBClear();
  }
  

  public static int DBCount(long dbHandle)
  {
    if (0L == dbHandle)
    {
      return FingerprintSensorErrorCode.ZKFP_ERR_INVALID_HANDLE;
    }
    return ZKFPService.DBCount();
  }
  

  public static int DBIdentify(long dbHandle, byte[] temp, int[] fid, int[] score)
  {
    if (0L == dbHandle)
    {
      return FingerprintSensorErrorCode.ZKFP_ERR_INVALID_HANDLE;
    }
    return ZKFPService.IdentifyFP(temp, fid, score);
  }
  

  public static int DBMatch(long dbHandle, byte[] temp1, byte[] temp2)
  {
    if (0L == dbHandle)
    {
      return FingerprintSensorErrorCode.ZKFP_ERR_INVALID_HANDLE;
    }
    return ZKFPService.MatchFP(temp1, temp2);
  }
  

  public static int ExtractFromImage(long dbHandle, String FileName, int DPI, byte[] template, int[] size)
  {	
	  
    if (0L == dbHandle)
    {
    	System.out.println("here");
      return FingerprintSensorErrorCode.ZKFP_ERR_INVALID_HANDLE;
    }
    return ZKFPService.ExtractFromImage(FileName, DPI, template, size);
  }
  

  public static String BlobToBase64(byte[] buf, int cbBuf)
  {
    return ZKFPService.BlobToBase64(buf, cbBuf);
  }
  
  public static int Base64ToBlob(String strBase64, byte[] buf, int cbBuf)
  {
    return ZKFPService.Base64ToBlob(strBase64, buf, cbBuf);
  }
}
