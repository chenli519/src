package com.arcgis.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.arcgis.R;
import com.arcgis.entity.XCRWEntity;
import com.arcgis.httputil.App;
import com.arcgis.httputil.KsoapValidateHttp;
import com.arcgis.httputil.NetUtils;
import com.arcgis.httputil.ToastUtil;
import com.esri.core.geometry.Point;

import org.java_websocket.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DZZHRWEditActivity extends Activity implements View.OnClickListener{

    private Button queryBtn=null;
    private TextView backtextview;
    private TextView titletextview;
    private TextView mapBtn;
    //任务类型
    TextView TextViewrwlx=null;
    //任务主题
    TextView TextViewrwzt= null;
    //任务内容
    TextView TextViewrwnr= null;
    //任务地点
    TextView TextViewrwdd= null;
    //任务经度
    TextView TextViewrwjd=null;
    //任务纬度
    TextView TextViewrwwd= null;
    //    //任务附件
//    TextView TextViewrwfj= null;
    //巡查结果内容
    TextView TextViewxcjgnr= null;
    //巡查结果附件
//    TextView TextViewxcjgfj= null;
    //任务状态
    TextView TextViewrwztai= null;
    //任务开始时间
    TextView TextViewrwkssj=null;
    //    //任务结束时间
//    static TextView TextViewrwjssj=null;
    //发送人
    TextView TextViewfsr=null;
    //接收人
    TextView TextViewjsr=null;
    //定位
//    Button locationBtn=null;
    //    //上传附件
//    Button uploadFJBtn=null;
    //上传任务照片
    Button zpBtn=null;

    private XCRWEntity mXCRWEntity;
    private Point point;
    // private List<Point> sbyd_pt_list=new ArrayList<Point>();
    //调用webservice
    private KsoapValidateHttp ksoap;

    private String kcbh;
    DecimalFormat df=null;
    //全局变量存储位置
    private App MyApp;
//
//    private String px=null;
//    private String py=null;
    private boolean isNetwork=false;
    //存储照片视频文件
    private File savePhototFile=null;
    //上传数据流
    byte[] photobs = null;
    byte[] photobs2 = null;
    String pictureName="";//全部相片名字符串
    String photoName = null;//单张相片名
    private TableRow row;
    String filepath;
    ArrayList<String> arrayName=new ArrayList<String>();
    ArrayList<String> arrayPath=new ArrayList<String>();
    String objId=null ;
    String UpPerson=null;
    String remark="";
    ProgressDialog progressdialog;
    private String id = "";
    private String type = "XCRW";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_xcrw_edit);
        isNetwork= NetUtils.isNetworkAvailable(this);
        App.getInstance().addActivity(this);

        backtextview=(TextView)findViewById(R.id.backBtn);
        backtextview.setOnClickListener(this);

        titletextview= (TextView) findViewById(R.id.NavigateTitle);
        titletextview.setText("巡查任务编辑");

        mapBtn= (TextView) findViewById(R.id.mapBtn);
        mapBtn.setOnClickListener(this);

        df=(DecimalFormat) NumberFormat.getInstance();
        df.setMaximumFractionDigits(8);

        MyApp=(App) this.getApplication();

//        locationBtn= (Button) findViewById(R.id.locationBtn);
//        locationBtn.setOnClickListener(this);
//        uploadFJBtn= (Button) findViewById(R.id.uploadFJBtn);
//        uploadFJBtn.setOnClickListener(this);
        zpBtn= (Button) findViewById(R.id.zpBtn);
        zpBtn.setOnClickListener(this);

        row = (TableRow) findViewById(R.id.row);//放图片的表格
        TextViewrwlx= (TextView) this.findViewById(R.id.TextViewrwlx);
        TextViewrwzt= (TextView) this.findViewById(R.id.TextViewrwzt);
        TextViewrwnr= (TextView) this.findViewById(R.id.TextViewrwnr);
        TextViewrwdd= (TextView) this.findViewById(R.id.TextViewrwdd);
        TextViewrwjd= (TextView) this.findViewById(R.id.TextViewrwjd);
        TextViewrwwd= (TextView) this.findViewById(R.id.TextViewrwwd);
        TextViewxcjgnr= (TextView) this.findViewById(R.id.TextViewxcjgnr);
        TextViewrwztai= (TextView) this.findViewById(R.id.TextViewrwztai);
        TextViewrwkssj= (TextView) this.findViewById(R.id.TextViewrwkssj);
//        TextViewrwjssj= (TextView) this.findViewById(R.id.TextViewrwjssj);
//        TextViewrwjssj.setOnClickListener(this);
        TextViewfsr= (TextView) this.findViewById(R.id.TextViewfsr);
        TextViewjsr= (TextView) this.findViewById(R.id.TextViewjsr);

        SharedPreferences sp = DZZHRWEditActivity.this.getSharedPreferences("LOGIN_INFO", DZZHRWEditActivity.this.MODE_PRIVATE);
        objId = sp.getString("PID",null);
        UpPerson = sp.getString("NAME",null);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int viewId=v.getId();
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        switch(viewId){
            case R.id.mapBtn:
                //上传照片
//                if(photobs==null){
//                    return;
//                }
                if(arrayPath!=null) {
                    //执行方法，循环上传并保存多张相片
                    for (String photoPath : arrayPath) {
                        String photoName = photoPath.substring(photoPath.lastIndexOf("/") + 1, photoPath.length());
                        AddDZVediosPicturesSync addKCVediosPicturesSync = new AddDZVediosPicturesSync(id, type, photoName, photoPath);//上传相片
                        try {
                            String rslt = addKCVediosPicturesSync.execute().get(2000, TimeUnit.SECONDS);
                            if (rslt != null) {
                                if (rslt.equals(photoName)) {
                                    pictureName += photoName + ";";
                                } else {
                                    ToastUtil.show(this, "图片保存失败");
                                }

                            } else {
                                ToastUtil.show(this, "服务器返回值为空");
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                        }

                    }
                }

                //保存编辑结果
                String bh=mXCRWEntity.getRWBH();
                String receiverid=mXCRWEntity.getRECEIVER_ID();
//                String rq=mXCRWEntity.getCOMPLETETIME();
                String savecontent=mXCRWEntity.getRESULTCONTENT();
                GetDZDataSync saveEditResult=new GetDZDataSync(bh,receiverid,savecontent,pictureName);
                try {
                    String resultStr=saveEditResult.execute().get(20, TimeUnit.SECONDS);
                    if(resultStr!=null && !resultStr.isEmpty()){
                        if(resultStr.equals("1")){

                            ToastUtil.show(this,"任务信息上传成功");
                            DZZHRWEditActivity.this.finish();
                        }
                        if(resultStr.equals("0")){
                            ToastUtil.show(this,"任务修改失败");
                        }
                    }else{
                        ToastUtil.show(this,"服务器没有返回值");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.backBtn:
                DZZHRWEditActivity.this.finish();
                break;
//            case R.id.locationBtn:
//                //定位任务点
//                Intent toMapIntent=new Intent();
//                toMapIntent.setClass(DZZHRWEditActivity.this,MainMap7Activity.class);
//                toMapIntent.putExtra("FROM","CENTERAT");
//                toMapIntent.putExtra("PX",this.px);
//                toMapIntent.putExtra("PY",this.py);
//                toMapIntent.putExtra("TEXT",mXCRWEntity.getRWBH());
//                startActivity(toMapIntent);
//                break;
//            case R.id.uploadFJBtn:
//                //上传附件
//                break;
            case R.id.zpBtn:
                //拍照
                String photoPath= Environment.getExternalStorageDirectory().getAbsolutePath() + "/BJApp/images";
                File photoDir = new File(photoPath);
                if (!photoDir.exists()) {
                    photoDir.mkdirs();
                }
                savePhototFile = new File(photoPath, "XCRW"+formatter.format(date) + ".jpg");
                photoName="XCRW"+formatter.format(date) + ".jpg";

                savePhototFile.delete();
                if (!savePhototFile.exists()) {
                    try {
                        savePhototFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                }
                Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(savePhototFile));
                startActivityForResult(photoIntent, 1);
                break;

            default:
                break;
        }
    }

    //此回传方法中执行相片压缩
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(1==requestCode && RESULT_OK==resultCode){

            FileInputStream photoIn = null;
            try {
                if (savePhototFile != null) {
                    photoIn = new FileInputStream(savePhototFile);
                }
                photobs = new byte[photoIn.available()];
                photoIn.read(photobs);
                photoIn.close();
                filepath = savePhototFile.toString();

                arrayName.add(photoName);
                arrayPath.add(filepath);

                Bitmap bm = getSmallBitmap(filepath);
                FileOutputStream b = null;
                b = new FileOutputStream(savePhototFile.toString());
                // 把数据写入文件...10 是压缩率，表示压缩90%; 如果不压缩是100，表示压缩率为0
                boolean iscompress = bm.compress(Bitmap.CompressFormat.JPEG, 20, b);
                if (iscompress) {
                    ImageView imageView = new ImageView(this);
                    TableRow.LayoutParams lp = new TableRow.LayoutParams(200, 200);  // , 1是可选写的
                    lp.setMargins(0, 0, 0, 0);
                    imageView.setLayoutParams(lp);
                    imageView.setImageBitmap(bm);
                    row.addView(imageView);

                }


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //按下的如果是BACK，同时没有重复
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            DZZHRWEditActivity.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = this.getIntent();
        if(intent!=null){
            mXCRWEntity= (XCRWEntity) intent.getSerializableExtra("XCRW");
            if(mXCRWEntity!=null){
//                this.px=mXCRWEntity.getX();
//                this.py=mXCRWEntity.getY();

                if(mXCRWEntity.getTASKTYPE().equals("灾害点巡查")) {
                    TextViewrwlx.setText("人工指派巡查任务");
                }else{
                    TextViewrwlx.setText("预警巡查任务");
                }
                TextViewrwzt.setText(mXCRWEntity.getTASKTITLE());
                TextViewrwnr.setText(mXCRWEntity.getTASKCONTENT());
                TextViewrwdd.setText(mXCRWEntity.getTASKAddress());
                TextViewrwjd.setText(mXCRWEntity.getE());
                TextViewrwwd.setText(mXCRWEntity.getN());
//                TextViewrwfj.setText(mXCRWEntity.getTASKFILES());
                TextViewxcjgnr.setText(mXCRWEntity.getRESULTCONTENT());
//                TextViewxcjgfj.setText(mXCRWEntity.getRESULTFILES());
                TextViewrwztai.setText(mXCRWEntity.getSTATE());
                TextViewrwkssj.setText(mXCRWEntity.getSENDTIME());
//                TextViewrwjssj.setText(mXCRWEntity.getCOMPLETETIME());
                TextViewfsr.setText(mXCRWEntity.getSENDER_ID());
                TextViewjsr.setText(mXCRWEntity.getRECEIVER_ID());
            }
        }
        if(!isNetwork){
            ToastUtil.show(this,"请检查网络连接");
            //return;
        }

    }
    //保存任务信息
    public class GetDZDataSync extends AsyncTask<String, Integer, String> {
        String bh;
                String receiverid;
//        String rq;
        String savecontent;
        String files;

        public GetDZDataSync(String bh,String receiverid,String content,String files) {
            this.bh=bh;
            this.receiverid=receiverid;
//            this.rq=rq;
            this.savecontent=content;
            this.files=files;
            ksoap=new KsoapValidateHttp(DZZHRWEditActivity.this);
        }

        @Override
        protected String doInBackground(String... str) {
            try {
                String AddRslt=ksoap.WebUpdateInspectionMission(bh, receiverid, savecontent, files);
                if(AddRslt!=null){
                    return AddRslt;
                }else{
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    //计算当前时间
    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            Log.d("OnDateSet", "select year:" + year + ";month:" + month + ";day:" + day);
            String m="";
            String d="";
            if(month<10){
                m="0"+(month+1)+"";
            }else{
                m=month+"";
            }

            if(day<10){
                d="0"+day+"";
            }else{
                d=day+"";
            }
//            TextViewrwjssj.setText(year+"-"+m+"-"+d);
        }
    }
    // Bitmap转换成byte[]
    public byte[] Bitmap2Bytes(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
    //通过路劲压缩图片
    public static Bitmap getSmallBitmap (String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile( filePath, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 480, 800);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }
    public static int calculateInSampleSize(BitmapFactory.Options options,int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height/ (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    public class AddDZVediosPicturesSync extends AsyncTask<String, Integer, String> {
        //    byte[] vediobyte;
//        String vedioname;
//        String videobsBase64;
        String photobsBase64;
        String id;
        String type;
        ArrayList<String> arrayNmae=new ArrayList<String>();
        ArrayList<String> arrayPath=new ArrayList<String>();
        String photoName;
        String photoPath;
        byte[] photobs2;

        public AddDZVediosPicturesSync(String id,String type,String photoName,String photoPath) {
            this.id=id;
            this.type=type;
            this.photoPath=photoPath;
            this.photoName=photoName;
            ksoap=new KsoapValidateHttp(DZZHRWEditActivity.this);
            photobs2=Bitmap2Bytes(BitmapFactory.decodeFile(photoPath));
            this.arrayNmae=arrayNmae;
            this.arrayPath=arrayPath;
//            this.vedioname=vedioname;
//            this.picturebyte=picture;
//            this.picname=picname;
            this.photoName=photoName;
            this.photobs2=photobs2;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... str) {

            try {
//
//                if(vediobyte!=null){
//                    videobsBase64= Base64.encodeBytes(vediobyte);
//                }
//
                if(photobs2!=null){
                    photobsBase64= Base64.encodeBytes(photobs2);
                }

//                if(videobsBase64==null){
//                    videobsBase64="";
//                }
                if(photobsBase64==null){
                    photobsBase64="";
                }

                String AddRslt=ksoap.DZWebUploadVediosPictures(id,type,photoName, photobsBase64);
                if(AddRslt!=null){
                    return AddRslt;
                }else{
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(progressdialog!=null){
                progressdialog.dismiss();
            }
        }

    }


}