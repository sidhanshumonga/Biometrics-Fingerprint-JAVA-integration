package com.hisp.biometric.main;


import com.hisp.biometric.models.FingerPrint;
import com.hisp.biometric.models.NetworkException;
import com.hisp.biometric.models.TrackedEntityInstance;
import com.hisp.biometric.util.BrowserConnection;
import com.hisp.biometric.util.ConfigurationAccess;
import com.hisp.biometric.util.NetworkCall;
import com.zkteco.biometric.FingerprintSensorErrorCode;
import com.zkteco.biometric.FingerprintSensorEx;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ZKFPDemo extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JButton btnOpen = null;
	JButton btnEnroll = null;
	JButton btnVerify = null;
	JButton btnIdentify = null;
	JButton btnRegImg = null;
	JButton btnIdentImg = null;
	JButton btnClose = null;
	JButton btnImg = null;
	JRadioButton radioISO = null;
	JRadioButton radioANSI = null;
        JTextField txtName = null;
	
	private JTextArea textArea;
	
	//the width of fingerprint image
	int fpWidth = 0;
	//the height of fingerprint image
	int fpHeight = 0;
	//for verify test
	private byte[] lastRegTemp = new byte[2048];
	//the length of lastRegTemp
	private int cbRegTemp = 0;
	//pre-register template
	private byte[][] regtemparray = new byte[3][2048];
	//Register
	private boolean bRegister = false;
	//Identify
	private boolean bIdentify = true;
	//finger id
	private int iFid = 1;
	
	private int nFakeFunOn = 1;
	//must be 3
	static final int enroll_cnt = 3;
	//the index of pre-register function
	private int enroll_idx = 0;
	
	private byte[] imgbuf = null;
	private byte[] template = new byte[2048];
	private int[] templateLen = new int[1];
	
	
	private boolean mbStop = true;
	private long mhDevice = 0;
	private long mhDB = 0;
	private WorkThread workThread = null;
        private FingerPrint lastFp;
        
	
	public void launchFrame(){
                System.out.println(System.getProperty("java.library.path"));
		this.setLayout (null);
		btnOpen = new JButton("Open");  
		this.add(btnOpen);  
		int nRsize = 20;
		btnOpen.setBounds(30, 10 + nRsize, 100, 30);
		
		btnEnroll = new JButton("Enroll");  
		this.add(btnEnroll);  
		btnEnroll.setBounds(30, 60 + nRsize, 100, 30);
		
		btnVerify = new JButton("Verify");  
		this.add(btnVerify);  
		btnVerify.setBounds(30, 110 + nRsize, 100, 30);
		
		btnIdentify = new JButton("Identify");  
		this.add(btnIdentify);  
		btnIdentify.setBounds(30, 160 + nRsize, 100, 30);
		
		btnRegImg = new JButton("Register By Image");  
		this.add(btnRegImg);  
		btnRegImg.setBounds(15, 210 + nRsize, 120, 30);
		
		btnIdentImg = new JButton("Verify By Image");  
		this.add(btnIdentImg);  
		btnIdentImg.setBounds(15, 260 + nRsize, 120, 30);
		
		
		btnClose = new JButton("Close");  
		this.add(btnClose);  
		btnClose.setBounds(30, 310 + nRsize, 100, 30);
		
		
		//For ISO/Ansi
		radioANSI = new JRadioButton("ANSI", true);// Ã¥Ë†â€ºÃ¥Â»ÂºÃ¥ï¿½â€¢Ã©â‚¬â€°Ã¦Å’â€°Ã©â€™Â®
		this.add(radioANSI);  
		radioANSI.setBounds(30, 360 + nRsize, 60, 30);
		
		radioISO = new JRadioButton("ISO");// Ã¥Ë†â€ºÃ¥Â»ÂºÃ¥ï¿½â€¢Ã©â‚¬â€°Ã¦Å’â€°Ã©â€™Â®
		this.add(radioISO);  
		radioISO.setBounds(120, 360 + nRsize, 60, 30);
        
                ButtonGroup group = new ButtonGroup();
                group = new ButtonGroup();
                group.add(radioANSI);
                group.add(radioISO);
              //For End
        
		btnImg = new JButton();
		btnImg.setBounds(150, 5, 256, 300);
		btnImg.setDefaultCapable(false);
		this.add(btnImg); 
		
                txtName  = new JTextField();
                txtName.setBounds(500,440,150,20);
                this.add(txtName);
                
                final JButton btnRegister = new JButton("Register");
                btnRegister.addActionListener(new ActionListener(){
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        gotoRegisterWindow(lastFp);
                    }
                });
                
                
                this.add(btnRegister);
                btnRegister.setBounds(500,480,150,20);
                
		textArea = new JTextArea();
		this.add(textArea);  
		textArea.setBounds(10, 440, 480, 100);
		
		
		this.setSize(700, 580);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.setTitle("ZKFinger Demo");
		this.setResizable(false);
		
		btnOpen.addActionListener(new ActionListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
                                long a =FingerprintSensorEx.DBInit();
                                if(a!=0)System.out.println("Success");
                                else System.out.println("fail");
				if (0 != mhDevice)
				{
					//already inited
					textArea.setText("Please close device first!");
					return;
				}
				int ret = FingerprintSensorErrorCode.ZKFP_ERR_OK;
				//Initialize
				cbRegTemp = 0;
				bRegister = false;
				bIdentify = false;
				iFid = 1;
				enroll_idx = 0;
				if (FingerprintSensorErrorCode.ZKFP_ERR_OK != FingerprintSensorEx.Init())
				{
					textArea.setText("Init failed!");
					return;
				}
				ret = FingerprintSensorEx.GetDeviceCount();
				if (ret < 0)
				{
					textArea.setText("No devices connected!");
					FreeSensor();
					return;
				}
				if (0 == (mhDevice = FingerprintSensorEx.OpenDevice(0)))
				{
					textArea.setText("Open device fail, ret = " + ret + "!");
					FreeSensor();
					return;
				}
				if (0 == (mhDB = FingerprintSensorEx.DBInit()))
				{
                                        
					textArea.setText("Init DB fail, ret = " + ret + "!");
					FreeSensor();
					return;
				}
                                /*DBConnect con = DBConnect.newInstance();
                                String sql = "select * from fingerprint";
                                List<Model> models = con.executeQuery(sql);
				for(Model model:models){
                                    int fid = model.getId();
                                    String temp = model.getTemplate();
                                    byte[] tempArray = new byte[2048];
                                    int result = FingerprintSensorEx.Base64ToBlob(temp, tempArray, 2048);
                                    System.out.println("ar "+tempArray);
                                    if(0 == (ret = FingerprintSensorEx.DBAdd(mhDB, fid, tempArray))){
                                       System.out.println("Success"+fid); 
                                    }else{
                                        System.out.println("Failed"+fid);
                                    }
                                }*/
                                    
				//For ISO/Ansi
				int nFmt = 0;	//Ansi
				if (radioISO.isSelected())
				{
					nFmt = 1;	//ISO
				}
				FingerprintSensorEx.DBSetParameter(mhDB,  5010, nFmt);				
				//For ISO/Ansi End
				
				//set fakefun off
				//FingerprintSensorEx.SetParameter(mhDevice, 2002, changeByte(nFakeFunOn), 4);
				
				byte[] paramValue = new byte[4];
				int[] size = new int[1];
				//GetFakeOn
				//size[0] = 4;
				//FingerprintSensorEx.GetParameters(mhDevice, 2002, paramValue, size);
				//nFakeFunOn = byteArrayToInt(paramValue);
				
				size[0] = 4;
				FingerprintSensorEx.GetParameters(mhDevice, 1, paramValue, size);
				fpWidth = byteArrayToInt(paramValue);
				size[0] = 4;
				FingerprintSensorEx.GetParameters(mhDevice, 2, paramValue, size);
				fpHeight = byteArrayToInt(paramValue);
				//width = fingerprintSensor.getImageWidth();
				//height = fingerprintSensor.getImageHeight();
				imgbuf = new byte[fpWidth*fpHeight];
				btnImg.resize(fpWidth, fpHeight);
				mbStop = false;
				workThread = new WorkThread();
			    workThread.start();// Ã§ÂºÂ¿Ã§Â¨â€¹Ã¥ï¿½Â¯Ã¥Å Â¨
	            textArea.setText("Open succ!");
			}
		});
		
		
		
		btnClose.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				FreeSensor();
				
				textArea.setText("Close succ!");
			}
		});
		
		btnEnroll.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(0 == mhDevice)
				{
					textArea.setText("Please Open device first!");
					return;
				}
				if(!bRegister)
				{
					enroll_idx = 0;
					bRegister = true;
					textArea.setText("Please your finger 3 times!");
				}
			}
			});
		
		btnVerify.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(0 == mhDevice)
				{
					textArea.setText("Please Open device first!");
					return;
				}
				if(bRegister)
				{
					enroll_idx = 0;
					bRegister = false;
				}
				if(bIdentify)
				{
					bIdentify = false;
				}
			}
			});
		
		btnIdentify.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(0 == mhDevice)
				{
					textArea.setText("Please Open device first!");
					return;
				}
				if(bRegister)
				{
					enroll_idx = 0;
					bRegister = false;
				}
				if(!bIdentify)
				{
					bIdentify = true;
				}
			}
			});
		
		
		btnRegImg.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(0 == mhDB)
				{
					textArea.setText("Please open device first!");
				}
				String path = "/fingerprint.bmp";
				byte[] fpTemplate = new byte[2048];
				int[] sizeFPTemp = new int[1];
				sizeFPTemp[0] = 2048;
				int ret = FingerprintSensorEx.ExtractFromImage( mhDB, path, 500, fpTemplate, sizeFPTemp);
				System.out.println(ret);
				if (0 == ret)
				{
					System.out.println(mhDB +" "+iFid +" "+fpTemplate);
                                        
					ret = FingerprintSensorEx.DBAdd( mhDB, iFid, fpTemplate);
					//System.out.println(iFid);
					if (0 == ret)
					{
						//String base64 = fingerprintSensor.BlobToBase64(fpTemplate, sizeFPTemp[0]);		
						iFid++;
                                                cbRegTemp = sizeFPTemp[0];
                                                System.arraycopy(fpTemplate, 0, lastRegTemp, 0, cbRegTemp);
                                                //Base64 Template
                                                //String strBase64 = Base64.encodeToString(regTemp, 0, ret, Base64.NO_WRAP);
                                                textArea.setText("enroll succ");
                                                btnRegister.setEnabled(true);
                                                btnRegister.setText("Register/Add");
					}
					else
					{
						textArea.setText("DBAdd fail, ret=" + ret);
					}
				}
				else
				{
					textArea.setText("ExtractFromImage fail, ret=" + ret);
				}
			}
			});
		
		
		btnIdentImg.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(0 ==  mhDB)
				{
					textArea.setText("Please open device first!");
				}
				String path = "/fingerprint.bmp";
				byte[] fpTemplate = new byte[2048];
				int[] sizeFPTemp = new int[1];
				sizeFPTemp[0] = 2048;
				int ret = FingerprintSensorEx.ExtractFromImage(mhDB, path, 500, fpTemplate, sizeFPTemp);
				//System.out.println(ret);
				if (0 == ret)
				{
					if (bIdentify)
					{
						int[] fid = new int[1];
						int[] score = new int [1];
						ret = FingerprintSensorEx.DBIdentify(mhDB, fpTemplate, fid, score);
                        if (ret == 0)
                        {
                        	textArea.setText("Identify succ, fid=" + fid[0] + ",score=" + score[0]);
                        }
                        else
                        {
                        	textArea.setText("Identify fail, errcode=" + ret);
                        }
                            
					}
					else
					{
						if(cbRegTemp <= 0)
						{
							textArea.setText("Please register first!");
						}
						else
						{
							ret = FingerprintSensorEx.DBMatch(mhDB, lastRegTemp, fpTemplate);
							if(ret > 0)
							{
								textArea.setText("Verify succ, score=" + ret);
							}
							else
							{
								textArea.setText("Verify fail, ret=" + ret);
							}
						}
					}
				}
				else
				{
					textArea.setText("ExtractFromImage fail, ret=" + ret);
				}
			}
		});
	
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.addWindowListener(new WindowAdapter(){

            @Override
            public void windowClosing(WindowEvent e) {
                // TODO Auto-generated method stub
            	FreeSensor();
            }
		});
	}
	
	private void FreeSensor()
	{
		mbStop = true;
		try {		//wait for thread stopping
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (0 != mhDB)
		{
			FingerprintSensorEx.DBFree(mhDB);
			mhDB = 0;
		}
		if (0 != mhDevice)
		{
			FingerprintSensorEx.CloseDevice(mhDevice);
			mhDevice = 0;
		}
		FingerprintSensorEx.Terminate();
	}
	
	public static void writeBitmap(byte[] imageBuf, int nWidth, int nHeight,
			String path) throws IOException {
		//System.out.println(path);
		java.io.FileOutputStream fos = new java.io.FileOutputStream(path);
		//System.out.println(fos);
		java.io.DataOutputStream dos = new java.io.DataOutputStream(fos);

		int w = (((nWidth+3)/4)*4);
		int bfType = 0x424d; 
		int bfSize = 54 + 1024 + w * nHeight;
		int bfReserved1 = 0;
		int bfReserved2 = 0;
		int bfOffBits = 54 + 1024;

		dos.writeShort(bfType);
		dos.write(changeByte(bfSize), 0, 4); 
		dos.write(changeByte(bfReserved1), 0, 2);
		dos.write(changeByte(bfReserved2), 0, 2);
		dos.write(changeByte(bfOffBits), 0, 4);

		int biSize = 40;
		int biWidth = nWidth;
		int biHeight = nHeight;
		int biPlanes = 1; 
		int biBitcount = 8;
		int biCompression = 0;
		int biSizeImage = w * nHeight;
		int biXPelsPerMeter = 0;
		int biYPelsPerMeter = 0;
		int biClrUsed = 0;
		int biClrImportant = 0;

		dos.write(changeByte(biSize), 0, 4);
		dos.write(changeByte(biWidth), 0, 4);
		dos.write(changeByte(biHeight), 0, 4);
		dos.write(changeByte(biPlanes), 0, 2);
		dos.write(changeByte(biBitcount), 0, 2);
		dos.write(changeByte(biCompression), 0, 4);
		dos.write(changeByte(biSizeImage), 0, 4);
		dos.write(changeByte(biXPelsPerMeter), 0, 4);
		dos.write(changeByte(biYPelsPerMeter), 0, 4);
		dos.write(changeByte(biClrUsed), 0, 4);
		dos.write(changeByte(biClrImportant), 0, 4);

		for (int i = 0; i < 256; i++) {
			dos.writeByte(i);
			dos.writeByte(i);
			dos.writeByte(i);
			dos.writeByte(0);
		}

		byte[] filter = null;
		if (w > nWidth)
		{
			filter = new byte[w-nWidth];
		}
		
		for(int i=0;i<nHeight;i++)
		{
			dos.write(imageBuf, (nHeight-1-i)*nWidth, nWidth);
			if (w > nWidth)
				dos.write(filter, 0, w-nWidth);
		}
		dos.flush();
		dos.close();
		fos.close();
	}

	public static byte[] changeByte(int data) {
		return intToByteArray(data);
	}
	
	public static byte[] intToByteArray (final int number) {
		byte[] abyte = new byte[4];  
	
	    abyte[0] = (byte) (0xff & number);  
	  abyte[1] = (byte) ((0xff00 & number) >> 8);  
	    abyte[2] = (byte) ((0xff0000 & number) >> 16);  
	    abyte[3] = (byte) ((0xff000000 & number) >> 24);  
	    return abyte; 
	}	 
		 
        public static int byteArrayToInt(byte[] bytes) {
                int number = bytes[0] & 0xFF;  
            number |= ((bytes[1] << 8) & 0xFF00);  
            number |= ((bytes[2] << 16) & 0xFF0000);  
            number |= ((bytes[3] << 24) & 0xFF000000);  
            return number;  
         }
	
        private class WorkThread extends Thread {
        @Override
        public void run() {
            super.run();
            int ret = 0;
            while (!mbStop) {
                templateLen[0] = 2048;
                if (0 == (ret = FingerprintSensorEx.AcquireFingerprint(mhDevice, imgbuf, template, templateLen)))
                {
                    if (nFakeFunOn == 1)
                    {
                        byte[] paramValue = new byte[4];
                        int[] size = new int[1];
                        size[0] = 4;
                        int nFakeStatus = 0;
                        //GetFakeStatus
                        ret = FingerprintSensorEx.GetParameters(mhDevice, 2004, paramValue, size);
                        nFakeStatus = byteArrayToInt(paramValue);
                        System.out.println("ret = "+ ret +",nFakeStatus=" + nFakeStatus);
                        if (0 == ret && (byte)(nFakeStatus & 31) != 31)
                        {
                                textArea.setText("Is a fake-finer?");
                                return;
                        }
                    }
                    System.out.println("height "+fpHeight);
                    System.out.println("width "+fpWidth);
                    OnCatpureOK(imgbuf);
                    OnExtractOK(template, templateLen[0]);
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
        }
		
        private void OnCatpureOK(byte[] imgBuf)
        {
                try {
                        System.out.println("parameters :");
                        System.out.println("Size of img Buf"+imgBuf.length);
                        System.out.println("fpWidth"+fpWidth);
                        System.out.println("fpheight"+fpHeight);
                        writeBitmap(imgBuf, fpWidth, fpHeight, "fingerprint.bmp");
                        
                        btnImg.setIcon(new ImageIcon(ImageIO.read(new File("fingerprint.bmp"))));
                } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
        }
		
        private void OnExtractOK(byte[] template, int len){
            if(bRegister)
            {
                int[] fid = new int[1];
                int[] score = new int [1];
                int ret = FingerprintSensorEx.DBIdentify(mhDB, template, fid, score);
                if (ret == 0)
                {
                    textArea.setText("the finger already enroll by " + fid[0] + ",cancel enroll");
                    bRegister = false;
                    enroll_idx = 0;
                    return;
                }
                if (enroll_idx > 0 && FingerprintSensorEx.DBMatch(mhDB, regtemparray[enroll_idx-1], template) <= 0)
                {
                    textArea.setText("please press the same finger 3 times for the enrollment");
                    return;
                }
                System.arraycopy(template, 0, regtemparray[enroll_idx], 0, 2048);
                enroll_idx++;
                if (enroll_idx == 3) {
                    int[] _retLen = new int[1];
                    _retLen[0] = 2048;
                    byte[] regTemp = new byte[_retLen[0]];

                    if (0 == (ret = FingerprintSensorEx.DBMerge(mhDB, regtemparray[0], regtemparray[1], regtemparray[2], regTemp, _retLen))) {//&&
                                //0 == (ret = FingerprintSensorEx.DBAdd(mhDB, iFid, regTemp))) {
                        iFid++;
                        cbRegTemp = _retLen[0];

                        String base641 = FingerprintSensorEx.BlobToBase64(regtemparray[0], _retLen[0]);
                        String base642 = FingerprintSensorEx.BlobToBase64(regtemparray[1], _retLen[0]);
                        String base643 = FingerprintSensorEx.BlobToBase64(regtemparray[2], _retLen[0]);
                        
                        
                        System.out.println("1 :" + base641 );
                        System.out.println("2 :" + base642 );
                        System.out.println("3 :" + base643 );
                        
                        
                        //System.out.println("Enrollment");
                        String regTempStr = FingerprintSensorEx.BlobToBase64(regTemp, _retLen[0]);
                        FingerPrint fp = new FingerPrint();
                        try{
                        fp = NetworkCall.recognize(regTempStr);
                        }catch(Exception ex){
                            ex.printStackTrace();
                            return;
                        }
                        if(fp==null){
                            textArea.setText("Error connecting to Server...");
                        }else if(fp.getFid()!=-1){
                            textArea.setText("Already registered : ID "+fp.getFid());
                        }else{
                            //sending enrollement request
                            textArea.setText("Enrolling");
                            try{
                                fp = NetworkCall.sendEnrollment(regTempStr);
                            }catch(NetworkException ex){
                                ex.printStackTrace();;
                                return;
                            }
                            if(fp==null){
                                textArea.setText("Error connecting to Server...");
                            }else if(fp.getFid()==-1){
                                textArea.setText("Enrollment Failed");
                            }else{
                                textArea.setText("Enrollment Success ID :"+fp.getFid());
                                lastFp = fp;
                            }
                        }
                        /*System.out.println("Enrolled STR TEMP b64 : "+regTempStr);
                        DBConnect con = DBConnect.newInstance();
                        String sql = "insert into fingerprint (template,name,tempint) values('"+regTempStr+"','"+txtName.getText()+"',1)";
                        //System.out.println(sql);
                        con.executeSQL(sql);
                        textArea.setText("enroll succ");*/
                    } else {
                        textArea.setText("enroll fail, error code=" + ret);
                    }
                    bRegister = false;
                } else {
                    textArea.setText("You need to press the " + (3 - enroll_idx) + " times fingerprint");
                }
            }else if (bIdentify){
                /*int[] fid = new int[1];
                int[] score = new int [1];
                int ret = FingerprintSensorEx.DBIdentify(mhDB, template, fid, score);
                System.out.println(template + " " + lastRegTemp + " " + cbRegTemp + " " + iFid);
                if (ret == 0)
                {
                    String strBase64 = Base64.getEncoder().encodeToString(template);
                    System.out.println("=");
                    System.out.println("Identify : "+strBase64);
                    //strBase64 = Base64.getEncoder().encodeToString(lastRegTemp);
                    //System.out.println("Identify : "+strBase64);
                    textArea.setText("Identify succ, fid=" + fid[0] + ",score=" + score[0]);
                }else{
                    textArea.setText("Identify fail, errcode=" + ret);
                }*/
                String templateStr = Base64.getEncoder().encodeToString(template);
                FingerPrint fp = new FingerPrint();
                try{
                    fp = NetworkCall.recognize(templateStr);
                }catch(NetworkException ex){
                    ex.printStackTrace();;
                    return;
                }
                if(fp==null){
                    textArea.setText("Error connecting to Server...");
                }else if(fp.getFid()!=-1){
                    textArea.setText("Recognised : ID "+fp.getFid());
                }else{
                    textArea.setText("Not Recognized Enrollment possible");
                }

            }else if(cbRegTemp <= 0){
                textArea.setText("Please register first!");
            }else{
                int ret = FingerprintSensorEx.DBMatch(mhDB, lastRegTemp, template);

                if(ret > 0)
                {
                    textArea.setText("Verify succ, score=" + ret);
                }   
                else
                {
                    textArea.setText("Verify fail, ret=" + ret);
                }
            }
                
            
        }
        
        public boolean gotoRegisterWindow(FingerPrint fp){
            String urlstr = NetworkCall.getUrlForRegister(fp);
            try{
                URL url = new URL(urlstr);
                BrowserConnection.launchBroswserfor(url);
                return true;
            }catch(MalformedURLException ex){
                System.out.println("Error forming the url");
                ex.printStackTrace();
            }           
            return false;
        
        }
        
        public boolean gotoDashBoard(TrackedEntityInstance tei){
            return true;
        }
		
		public static void main(String[] args) {
			new ZKFPDemo().launchFrame();
		}
}
