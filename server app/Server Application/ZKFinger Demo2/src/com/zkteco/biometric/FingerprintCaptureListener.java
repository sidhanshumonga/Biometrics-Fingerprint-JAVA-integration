package com.zkteco.biometric;

public abstract interface FingerprintCaptureListener
{
  public abstract void captureOK(byte[] paramArrayOfByte);
  
  public abstract void captureError(int paramInt);
  
  public abstract void extractOK(byte[] paramArrayOfByte);
}
