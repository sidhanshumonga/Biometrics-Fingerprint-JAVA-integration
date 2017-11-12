package com.zkteco.biometric;

import java.util.Map;

public class FingerprintSensor
{
  private long handle = 0L;
  private int width = 0;
  private int height = 0;
  private int lastTempLen = 0;
  private boolean bopened = false;
  private boolean bstarted = false;
  private int index = -1;
  private String devSn = "";
  private int FakeFunOn = 1;
  
  protected String deviceTag = String.valueOf(new java.util.Random().nextInt());
  public static final String KEY_CAPTURE_LISTENER_PREFIX = "key.working.listener.";
  private Map<String, FingerprintCaptureListener> fingerVeinCaptureListenerList = new java.util.HashMap<String, FingerprintCaptureListener>();
  
  public FingerprintSensor()
  {
    ZKFPService.Initialize();
  }
  
  public void setLastTempLen(int len)
  {
    lastTempLen = len;
  }
  
  public int getLastTempLen()
  {
    return lastTempLen;
  }
  
  public int getImageWidth()
  {
    return width;
  }
  
  public int getImageHeight()
  {
    return height;
  }
  
  public String getDevSn()
  {
    return devSn;
  }
  
  public Map<String, FingerprintCaptureListener> getFingerprintCaptureListenerList()
  {
    return fingerVeinCaptureListenerList;
  }
  
  public void setFingerprintCaptureListener(FingerprintCaptureListener listener)
  {
    fingerVeinCaptureListenerList.put("key.working.listener." + index, listener);
  }
  
  public String getDeviceTag() {
    return deviceTag;
  }
  
  public boolean startCapture()
  {
    if ((index < 0) || (bstarted)) return false;
    if ((fingerVeinCaptureListenerList.size() > 0) && (index < fingerVeinCaptureListenerList.size())) {
      FingerprintCaptureThreadPool.start(this, index);
      System.out.println("Start fingerprint capture thread " + index + " OK");
      bstarted = true;
      return true;
    }
    System.out.println("Start fingerprint capture thread failed!");
    return false;
  }
  

  public void destroy()
  {
    
    if (bopened)
    {
      closeDevice();
    }
    ZKFPService.Finalize();
  }
  
  public void stopCapture()
  {
    if (!bstarted)
      return;
    if ((fingerVeinCaptureListenerList.size() > 0) && (index < fingerVeinCaptureListenerList.size())) {
      FingerprintCaptureThreadPool.cancel(this, index);
    } else {
      System.out.println("Stop fingerprint capture thread failed!");
    }
    bstarted = false;
  }
  
  public int getDeviceCount()
  {
    return ZKFPService.GetDeviceCount();
  }
  
  public int openDevice(int index)
  {
    handle = ZKFPService.OpenDevice(index);
    if (handle == 0L)
    {
      return FingerprintSensorErrorCode.ERROR_OPEN_FAIL;
    }
    int[] retWidth = new int[1];
    int[] retHeight = new int[1];
    ZKFPService.GetCapParams(handle, retWidth, retHeight);
    width = retWidth[0];
    height = retHeight[0];
    
    byte[] value = new byte[64];
    int[] retLen = new int[1];
    retLen[0] = 64;
    ZKFPService.GetParameter(handle, 1103, value, retLen);
    devSn = new String(value);
    this.index = index;
    return FingerprintSensorErrorCode.ERROR_SUCCESS;
  }
  
  public int closeDevice()
  {
    stopCapture();
    if (0L != handle)
    {
      ZKFPService.CloseDevice(handle);
      handle = 0L;
    }
    bopened = false;
    index = -1;
    return FingerprintSensorErrorCode.ERROR_SUCCESS;
  }
  
  public int capture(byte[] image, byte[] template, int[] templen)
  {
    if (0L == handle)
    {
      return FingerprintSensorErrorCode.ERROR_NOT_OPENED;
    }
    return ZKFPService.AcquireTemplate(handle, image, template, templen);
  }
  
  public void setFakeFunOn(int FakeFunOn)
  {
    if (0L != handle)
    {
      this.FakeFunOn = FakeFunOn;
      byte[] value = new byte[4];
      value[0] = ((byte)(FakeFunOn & 0xFF));
      value[1] = ((byte)((FakeFunOn & 0xFF00) >> 8));
      value[2] = ((byte)((FakeFunOn & 0xFF0000) >> 16));
      value[3] = ((byte)((FakeFunOn & 0xFF000000) >> 24));
      ZKFPService.SetParameter(handle, 2002, value, 4);
    }
  }
  
  public int getFakeFunOn()
  {
    return FakeFunOn;
  }
  
  public int getFakeStatus()
  {
    int status = -1;
    byte[] value = new byte[4];
    int[] retlen = new int[1];
    retlen[0] = 4;
    if (ZKFPService.GetParameter(handle, 2004, value, retlen) == 0)
    {
      status = value[0] & 0xFF;
      status += (value[1] << 8 & 0xFF00);
      status += (value[2] << 16 & 0xFF0000);
      status += (value[3] << 24 & 0xFF000000);
    }
    return status;
  }
  
  public int GenRegFPTemplate(byte[] temp1, byte[] temp2, byte[] temp3, byte[] regTemp, int[] regTempLen)
  {
    return ZKFPService.GenRegFPTemplate(temp1, temp2, temp3, regTemp, regTempLen);
  }
  
  public int DBAdd(int fid, byte[] regTemplate)
  {
    return ZKFPService.DBAdd(fid, regTemplate);
  }
  
  public int DBDel(int fid)
  {
    return ZKFPService.DBDel(fid);
  }
  
  public int DBCount()
  {
    return ZKFPService.DBCount();
  }
  
  public int VerifyFPByID(int fid, byte[] template)
  {
    return ZKFPService.VerifyFPByID(fid, template);
  }
  
  public int MatchFP(byte[] temp1, byte[] temp2)
  {
    return ZKFPService.MatchFP(temp1, temp2);
  }
  
  public int IdentifyFP(byte[] template, int[] fid, int[] socre)
  {
    return ZKFPService.IdentifyFP(template, fid, socre);
  }
  
  public int ExtractFromImage(String filePath, int DPI, byte[] template, int[] size)
  {
    return ZKFPService.ExtractFromImage(filePath, DPI, template, size);
  }
  
  public int GetParameter(int code, byte[] value, int[] len)
  {
    if (0L == handle)
    {
      return FingerprintSensorErrorCode.ERROR_NOT_OPENED;
    }
    return ZKFPService.GetParameter(handle, code, value, len);
  }
  
  public int SetParameter(int code, byte[] value, int len)
  {
    if (0L == handle)
    {
      return FingerprintSensorErrorCode.ERROR_NOT_OPENED;
    }
    int ret = ZKFPService.SetParameter(handle, code, value, len);
    if ((ret == 0) && (2002 == code))
    {
      FakeFunOn = (value[0] & 0xFF);
      FakeFunOn += (value[1] << 8 & 0xFF00);
      FakeFunOn += (value[2] << 16 & 0xFF0000);
      FakeFunOn += (value[3] << 24 & 0xFF000000);
    }
    if ((ret == 0) && (3 == code))
    {
      int[] retWidth = new int[1];
      int[] retHeight = new int[1];
      ZKFPService.GetCapParams(handle, retWidth, retHeight);
      width = retWidth[0];
      height = retHeight[0];
    }
    return ret;
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
