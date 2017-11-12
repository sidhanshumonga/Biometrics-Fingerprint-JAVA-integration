/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hisp.biometric.main;

import com.hisp.biometric.login.LoginFrame;
import static com.hisp.biometric.main.ZKFPDemo.byteArrayToInt;
import static com.hisp.biometric.main.ZKFPDemo.writeBitmap;
import com.hisp.biometric.models.FingerPrint;
import com.hisp.biometric.models.NetworkException;
import com.hisp.biometric.models.TrackedEntityInstance;
import com.hisp.biometric.util.BrowserConnection;
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

/**
 *
 * @author dell
 */
public class MainFrame extends javax.swing.JFrame {
    
    private static final long serialVersionUID = 1L;
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
	private MainFrame.WorkThread workThread = null;
        
        private FingerPrint lastFp;
        private TrackedEntityInstance teiValidated;
        
        private boolean newTei = false;
        private boolean updateTei = false;
        
        private boolean registerRequest = false;
    /**
     * Creates new form NewJFrame1
     */
    public MainFrame() {
        this.setUndecorated(true);
        initComponents();
    }
    
    public void launchFrame(){
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        btnVerify.setVisible(false);
                btnClose.addActionListener(new ActionListener(){
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        FreeSensor();
			textArea.setText("Close success!");
                        
                        btnOpen.setEnabled(true);
                        btnEnroll.setEnabled(false);
                        btnIdentify.setEnabled(false);
                        btnVerify.setEnabled(false);
                        rdNew.setEnabled(false);
                        rdUpdate.setEnabled(false);
                        txtName.setEditable(false);
                        btnValidate.setEnabled(false);
                        btnRegister.setEnabled(false);
                        btnClose.setEnabled(false);
                    }
                    
                });
                
                btnLogout.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        FreeSensor();
                        MainFrame.this.setVisible(false);
                        NetworkCall.getClientConfiguration().setUserName("");
                        NetworkCall.getClientConfiguration().setPassword("");
                        LoginFrame.start();
                        MainFrame.this.dispose();
                    }
                });
        
                buttonGroup1.add(rdNew);
                buttonGroup1.add(rdUpdate);
                rdNew.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if(rdNew.isSelected()) {
                            newTei = true;
                            updateTei = false;
                            txtName.setEditable(false);
                            btnValidate.setEnabled(false);
                            btnRegister.setEnabled(true);
                            btnRegister.setText("Register Patient");
                        }
                    }
                });
                rdUpdate.addActionListener(new ActionListener(){
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if(rdUpdate.isSelected()){
                            updateTei = true;
                            newTei = false;
                            txtName.setEditable(true);
                            txtName.setEnabled(true);
                            btnValidate.setEnabled(true);
                            btnRegister.setEnabled(false);
                            btnRegister.setText("Update Fingerprint");
                        }
                    }
                    
                });
                
                btnRegister.addActionListener(new ActionListener(){
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if(bIdentify){
                            TrackedEntityInstance tei;
                            try{
                                 tei = NetworkCall.getTrackedEntityInstanceWithfid(lastFp.getFid()+"");
                            }catch(NetworkException ex){
                                JOptionPane.showMessageDialog(MainFrame.this, ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            if(tei!=null){
                                gotoDashboard(tei);
                            }else if(registerRequest){
                                if(newTei){
                                    if(gotoRegisterWindow(lastFp)) {
                                        textArea.setText("Continue in browser");
                                    }
                                }else if(updateTei){
                                    if(lastFp==null) {JOptionPane.showMessageDialog(MainFrame.this,
                                            "Please Enroll your finger","Error",JOptionPane.ERROR_MESSAGE);
                                    }else if(teiValidated==null){
                                        JOptionPane.showMessageDialog(MainFrame.this,
                                            "Please Validate your Code","Error",JOptionPane.ERROR_MESSAGE);

                                    }else{
                                        textArea.setText("Updating please Wait...");
                                        boolean result;
                                        try{
                                            result = NetworkCall.updateFingerPrint(lastFp, teiValidated);
                                        }catch(NetworkException ex){
                                            textArea.setText("Error.. try again");
                                            JOptionPane.showMessageDialog(MainFrame.this, ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
                                            return;
                                        }
                                        if(result){
                                            JOptionPane.showMessageDialog(MainFrame.this, 
                                                "Fingerprint Updated","Success",JOptionPane.INFORMATION_MESSAGE);
                                            textArea.setText("Fingerprint Updated");
                                            teiValidated = null;
                                            btnRegister.setEnabled(false);
                                            txtName.setText("");
                                        }else{
                                            JOptionPane.showMessageDialog(MainFrame.this,
                                            "Failed to update Please Retry","Error",JOptionPane.ERROR_MESSAGE);
                                            textArea.setText("Update Failed");
                                        }
                                        btnRegister.setEnabled(false);
                                        rdNew.setEnabled(false);
                                        rdUpdate.setEnabled(false);
                                        txtName.setEditable(false);
                                    }
                                }else{
                                    JOptionPane.showMessageDialog(MainFrame.this, "Please select one option","Option",JOptionPane.INFORMATION_MESSAGE);
                                }
                            }else{
                                int showConfirmDialog = JOptionPane.showConfirmDialog(MainFrame.this,"TEI not found you, Do you want to Register or update the fingerprint ?","Confirm",JOptionPane.YES_NO_OPTION);
                                if(showConfirmDialog==JOptionPane.YES_OPTION){
                                    registerRequest = true;
                                    rdNew.setEnabled(true);
                                    rdUpdate.setEnabled(true);
                                    btnRegister.setEnabled(true);
                                    btnRegister.setText("Register/Update");
                                }
                                
                                //JOptionPane.showMessageDialog(MainFrame.this,"TEI not found please register", "Error",JOptionPane.INFORMATION_MESSAGE);
                            }
                        }else if(registerRequest){
                                if(newTei){
                                    if(gotoRegisterWindow(lastFp)) {
                                        textArea.setText("Continue in browser");
                                    }
                                }else if(updateTei){
                                    if(lastFp==null) {JOptionPane.showMessageDialog(MainFrame.this,
                                            "Please Enroll your finger","Error",JOptionPane.ERROR_MESSAGE);
                                    }else if(teiValidated==null){
                                        JOptionPane.showMessageDialog(MainFrame.this,
                                            "Please Validate your Code","Error",JOptionPane.ERROR_MESSAGE);

                                    }else{
                                        textArea.setText("Updating please Wait...");
                                        boolean result;
                                        try{
                                            result = NetworkCall.updateFingerPrint(lastFp, teiValidated);
                                        }catch(NetworkException ex){
                                            textArea.setText("Error... Try again");
                                            JOptionPane.showMessageDialog(MainFrame.this, ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
                                            return;
                                        }
                                        if(result){
                                            JOptionPane.showMessageDialog(MainFrame.this, 
                                                "Update Success","Success",JOptionPane.INFORMATION_MESSAGE);
                                            textArea.setText("Update Success");
                                             teiValidated = null;
                                            btnRegister.setEnabled(false);
                                            txtName.setText("");
                                        }else{
                                            JOptionPane.showMessageDialog(MainFrame.this,
                                            "Failed to update Please Retry","Error",JOptionPane.ERROR_MESSAGE);
                                            textArea.setText("Update Failed");
                                        }
                                    }
                                }else{
                                    JOptionPane.showMessageDialog(MainFrame.this, "Please select one option","Option",JOptionPane.INFORMATION_MESSAGE);
                                }
                        }
                    }
                });
                
                btnValidate.addActionListener(new ActionListener(){
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if(!txtName.getText().equals("")){
                            textArea.setText("validating ...");
                            TrackedEntityInstance tei;
                            try{
                                tei = NetworkCall.getTrackedEntityInstanceWithCode(txtName.getText().trim());
                            }catch(NetworkException ex){
                                textArea.setText("Validation Faild.. try again");
                                JOptionPane.showMessageDialog(MainFrame.this, ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            if(tei!=null){
                                textArea.setText("Patient found \n Patient \t:"+tei.getLastName() +"\n ,Client Code \t:"+tei.getCode()+", was registered at "+tei.getOrgUnit());
                                teiValidated = tei;
                                btnRegister.setEnabled(true);
                            }else{
                                textArea.setText("Patient found");
                            }
                        }
                    }
                }
                );
                
		this.setVisible(true);
		this.setTitle("HISP Biometric");
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
				//btnImg.resize(fpWidth, fpHeight);
				mbStop = false;
				workThread = new MainFrame.WorkThread();
                                workThread.start();// Ã§ÂºÂ¿Ã§Â¨â€¹Ã¥ï¿½Â¯Ã¥Å Â¨
                                textArea.setText("Device Connected Successfully");
                                txtName.setEditable(false);
                                btnOpen.setEnabled(false);
                                btnEnroll.setEnabled(true);
                                btnIdentify.setEnabled(true);
                                //btnVerify.setEnabled(true);
                                //rdNew.setEnabled(true);
                                //rdUpdate.setEnabled(true);
                                btnClose.setEnabled(true);
                    
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
                                        bIdentify=false;
                                        registerRequest = false;
					textArea.setText("Please your finger 3 times!");
				}
                                teiValidated=null;
                                rdUpdate.setSelected(false);
                                rdNew.setSelected(false);
                                rdNew.setEnabled(true);
                                rdUpdate.setEnabled(true);
                                txtName.setEditable(true);
                                btnRegister.setEnabled(false);
                                btnRegister.setText("Register/Update");
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
                                registerRequest = false;
                                txtName.setEditable(false);
                                rdNew.setEnabled(false);
                                rdUpdate.setEnabled(false);
                                btnRegister.setText("Goto Dashboard");
                                btnRegister.setEnabled(false);
                                textArea.setText("Please place your finger");
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
                        }catch(NetworkException ex){
                            JOptionPane.showMessageDialog(MainFrame.this, ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        if(fp==null){
                            textArea.setText("Error connecting to Server...");
                        }else if(fp.getFid()!=-1){
                            textArea.setText("Already registered : ID "+fp.getFid());
                            btnRegister.setEnabled(false);
                            rdNew.setEnabled(false);
                            rdUpdate.setEnabled(false);
                            rdNew.setSelected(false);
                            rdUpdate.setSelected(false);
                            
                            btnValidate.setEnabled(false);
                            
                        }else{
                            //sending enrollement request
                            textArea.setText("Enrolling");
                            try{
                                fp = NetworkCall.sendEnrollment(regTempStr);
                            }catch(NetworkException ex){
                                JOptionPane.showMessageDialog(MainFrame.this, ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            if(fp==null){
                                textArea.setText("Error connecting to Server...");
                                btnRegister.setEnabled(false);
                            }else if(fp.getFid()==-1){
                                textArea.setText("Enrollment Failed");
                                btnRegister.setEnabled(false);
                            }else{
                                //textArea.setText("Enrollment Success ID :"+fp.getFid());
                                textArea.setText("Fingerprint enrollment success click on Register/Update button");
                                lastFp = fp;
                                btnRegister.setText("Register/Update");
                                btnRegister.setEnabled(true);
                                registerRequest = true;
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
                    textArea.setText("You need to press the finger " + (3 - enroll_idx) + "more times");
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
                //String templateStr = Base64.getEncoder().encodeToString(template);
                String templateStr = FingerprintSensorEx.BlobToBase64(template, 2048);
                FingerPrint fp = new FingerPrint();
                try{
                    fp = NetworkCall.recognize(templateStr);
                }catch(NetworkException ex){
                    JOptionPane.showMessageDialog(MainFrame.this, ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if(fp==null){
                    textArea.setText("Error connecting to Server...");
                }else if(fp.getFid()!=-1){
                    //textArea.setText("Recognised : ID "+fp.getFid());
                    textArea.setText("Fingerprint Recognized");
                    lastFp = fp;
                    btnRegister.setText("Go to Dashboard");
                    btnRegister.setEnabled(true);
                }else{
                    //textArea.setText("Not Recognized Enrollment possible");
                    textArea.setText("Fingerprint not recognized please add/upate patient");
                    btnRegister.setEnabled(false);
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
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        btnLogout = new javax.swing.JButton();
        btnImg = new javax.swing.JLabel();
        btnOpen = new javax.swing.JButton();
        btnVerify = new javax.swing.JButton();
        btnIdentify = new javax.swing.JButton();
        btnEnroll = new javax.swing.JButton();
        btnRegister = new javax.swing.JButton();
        txtName = new javax.swing.JTextField();
        btnValidate = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        textArea = new javax.swing.JTextArea();
        rdNew = new javax.swing.JRadioButton();
        rdUpdate = new javax.swing.JRadioButton();
        btnClose = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("HISP Biometric");
        setResizable(false);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        jPanel2.setEnabled(false);
        jPanel2.setPreferredSize(new java.awt.Dimension(700, 550));

        jPanel1.setBackground(new java.awt.Color(20, 84, 148));

        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 27)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("HIV Care and ART Tracking System");

        btnLogout.setBackground(new java.awt.Color(255, 255, 255));
        btnLogout.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        btnLogout.setText("Logout");
        btnLogout.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLogout.setOpaque(false);
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 575, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnLogout)
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnLogout)
                .addContainerGap())
        );

        btnImg.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(51, 51, 51), new java.awt.Color(204, 204, 204)));

        btnOpen.setBackground(new java.awt.Color(120, 144, 180));
        btnOpen.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        btnOpen.setText("Connect Device");
        btnOpen.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnOpen.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenActionPerformed(evt);
            }
        });

        btnVerify.setBackground(new java.awt.Color(120, 144, 180));
        btnVerify.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        btnVerify.setText("Verify");
        btnVerify.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnVerify.setEnabled(false);
        btnVerify.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        btnIdentify.setBackground(new java.awt.Color(120, 144, 180));
        btnIdentify.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        btnIdentify.setText("Identify Existing Patient");
        btnIdentify.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnIdentify.setEnabled(false);
        btnIdentify.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        btnEnroll.setBackground(new java.awt.Color(120, 144, 180));
        btnEnroll.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        btnEnroll.setText("Add / Update Patient");
        btnEnroll.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEnroll.setEnabled(false);
        btnEnroll.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        btnRegister.setBackground(new java.awt.Color(120, 144, 180));
        btnRegister.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        btnRegister.setText("Enroll Patient");
        btnRegister.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRegister.setEnabled(false);
        btnRegister.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        btnValidate.setText("Validate Client Code");
        btnValidate.setEnabled(false);

        textArea.setColumns(20);
        textArea.setRows(5);
        textArea.setText("Status: ");
        jScrollPane1.setViewportView(textArea);

        rdNew.setText("Create New Patient");
        rdNew.setEnabled(false);

        rdUpdate.setText("Update Existing Patient Fingerprint");
        rdUpdate.setEnabled(false);

        btnClose.setText("Disconnect Device");
        btnClose.setEnabled(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(267, 267, 267)
                        .addComponent(btnVerify, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(btnEnroll, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnIdentify, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                        .addComponent(btnOpen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(btnRegister, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(rdUpdate)
                                            .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(btnValidate)
                                            .addComponent(rdNew, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnImg, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 636, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(58, 58, 58)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnOpen, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(26, 26, 26)
                        .addComponent(btnIdentify, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(btnEnroll, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rdNew)
                            .addComponent(rdUpdate))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnValidate, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnRegister, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(btnImg, javax.swing.GroupLayout.PREFERRED_SIZE, 332, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnVerify, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 710, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 528, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnLogoutActionPerformed

    private void btnOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnOpenActionPerformed

    /**
     * @param args the command line arguments
     */
   public boolean gotoRegisterWindow(FingerPrint fp){
            String urlstr = NetworkCall.getUrlForRegister(fp);
            try{
                URL url = new URL(urlstr);
                System.out.println(url.toString());
                BrowserConnection.launchBroswserfor(url);
                return true;
            }catch(MalformedURLException ex){
                System.out.println("Error forming the url");
                ex.printStackTrace();
            }           
            return false;
        
        }
   
   public boolean gotoDashboard(TrackedEntityInstance tei){
       String urlstr = NetworkCall.getUrlForDashboard(tei);
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
        
    
		
		public static void main(String[] args) {
			new MainFrame().launchFrame();
		}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnEnroll;
    private javax.swing.JButton btnIdentify;
    private javax.swing.JLabel btnImg;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnOpen;
    private javax.swing.JButton btnRegister;
    private javax.swing.JButton btnValidate;
    private javax.swing.JButton btnVerify;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JRadioButton rdNew;
    private javax.swing.JRadioButton rdUpdate;
    private javax.swing.JTextArea textArea;
    private javax.swing.JTextField txtName;
    // End of variables declaration//GEN-END:variables
}
