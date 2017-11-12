package com.zkteco.biometric;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class FingerprintCaptureThread implements Runnable
{
  private FingerprintSensor fingerprintSensor = null;
  private FingerprintCaptureListener fingerprintCaptureListener = null;
  private boolean isCancel = false;
  private CountDownLatch countdownLatch = new CountDownLatch(1);
  
  public FingerprintCaptureThread(FingerprintSensor fingerprintSensor, int index) {
    this.fingerprintSensor = fingerprintSensor;
    fingerprintCaptureListener = ((FingerprintCaptureListener)fingerprintSensor.getFingerprintCaptureListenerList().get("key.working.listener." + index));
  }
  
  public void run()
  {
    while (!isCancel) {
      try {
        Thread.sleep(100L);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      
      byte[] fpImage = new byte[fingerprintSensor.getImageWidth() * fingerprintSensor.getImageHeight()];
      System.out.println(fpImage);
      byte[] fpTemplate = new byte['ࠀ'];
      System.out.println(fpTemplate);
      int ret = 0;
      int[] tempLen = new int[1];
      tempLen[0] = 2048;
      ret = fingerprintSensor.capture(fpImage, fpTemplate, tempLen);
      if (ret < 0)
      {
        fingerprintCaptureListener.captureError(ret);
      }
      else
      {
        fingerprintSensor.setLastTempLen(tempLen[0]);
        

        fingerprintCaptureListener.captureOK(fpImage);
        fingerprintCaptureListener.extractOK(fpTemplate);
      }
    }
    
    countdownLatch.countDown();
  }
  
  public void cancel() {
    isCancel = true;
    try {
      countdownLatch.await(2L, TimeUnit.SECONDS);
    }
    catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
  
  public boolean isCancel() {
    return isCancel();
  }
}
