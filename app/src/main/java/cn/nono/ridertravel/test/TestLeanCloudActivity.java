package cn.nono.ridertravel.test;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.avos.avoscloud.AVFile;

import cn.nono.ridertravel.R;
import cn.nono.ridertravel.ui.base.BaseNoTitleActivity;

/**
 * Created by Administrator on 2016/4/11.
 */
public class TestLeanCloudActivity extends BaseNoTitleActivity implements View.OnClickListener{


    Button addNativePhotoBtn;
    ImageView nativePhotoImageView;

    Button getNetPhotoBtn;
    ImageView netPhotoImageView;

    Button testRelationBtn;
    Button testArrBtn;

    String photoPath = null;
    Bitmap photoBitmap = null;
    boolean hasPhoto = false;

    AVFile avFile = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_lean_cloud);

        testRelationBtn = (Button) findViewById(R.id.test_avrelation_btn);
        testRelationBtn.setOnClickListener(this);

        testArrBtn = (Button) findViewById(R.id.test_add_arr_btn);
        testArrBtn.setOnClickListener(this);

        addNativePhotoBtn = (Button) findViewById(R.id.add_native_photo_btn);
        addNativePhotoBtn.setOnClickListener(this);
        nativePhotoImageView = (ImageView) findViewById(R.id.native_photo_imageview);

        getNetPhotoBtn = (Button) findViewById(R.id.get_net_photo_btn);
        getNetPhotoBtn.setOnClickListener(this);
        netPhotoImageView = (ImageView) findViewById(R.id.net_photo_imageview);

    }

    @Override
    public void onClick(View v) {
        /*
        int viewId = v.getId();
        if(R.id.add_native_photo_btn == viewId) {
                addNativePhoto();
        } else if(R.id.get_net_photo_btn == viewId) {
            getNetPhoto();
        } else if (R.id.test_avrelation_btn == viewId) {
            testAVRelation();
        } else if (R.id.test_add_arr_btn == viewId) {
            testAddArr();
        }
        */

    }

/*
    class CuploadThread extends  Thread {
        List<AVMediaItem> list;
        boolean done = false;

        public boolean getCState(){
            return done;
        }

        public CuploadThread(List<AVMediaItem> list) {
            super();
            this.list = list;
        }


        @Override
        public void run() {

            for (int i = 0; i < list.size(); i++) {
                try {
                  AVMediaItem avM =  list.get(i);
                    Log.i("xx",avM.getPhoto()+"");

                    avM.save();
                } catch (AVException e) {
                    e.printStackTrace();
                    Log.i("xx","e:"+e.getMessage());
                    break;
                }
            }

            done = true;
        }
    }

    private void testAddArr() {


//        saveInMultiThread();


        saveInSigalThread();

        //结论。无论你用单线程或者多线程 上传的速度基本差别不大。应该是leanCloud内部做了统一处理。


    }

    private void saveInMultiThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {

//                11:19:55.346
//                11:21:28.506

//                  33+60

//                11:57:21.516
//                12:00:44.706    23+180S 轮训成绩第一次


//                12:02:27.736
//                12:05:58.306  31+180S 轮训第二次



//                11:41:10.696
//                11:44:25.646       15+180S 信号量成绩. 第一次



//                11:47:02.376
//                11:50:23.766                11:50:23.766 信号量成绩   第二次




//                Semaphore semaphore = new Semaphore(0);





                String path = "/storage/emulated/0/Resource/Coolpad/Image_show/show_01/CoolShow_A000022.jpg";

                Log.i("xx", "timeS:" + System.currentTimeMillis());
                AVObject avObject = new AVObject("t1t");

                List<AVMediaItem> mediaItem1s = new ArrayList<AVMediaItem>();
                List<AVMediaItem> mediaItem2s = new ArrayList<AVMediaItem>();
                List<AVMediaItem> mediaItemAll = new ArrayList<AVMediaItem>();
                for (int i = 0; i < 50; i++) {
                    AVMediaItem avMediaItem = new AVMediaItem();
                    avMediaItem.setDescription("xxxx");
                    avMediaItem.setPhoto(new AVFile("photo", ImageUtil.autoCompressImage2byte(path)));
                    avMediaItem.setMediaType(AVMediaItem.MEDIATYPE_ONLY_DESCRIPTION);

                    mediaItemAll.add(avMediaItem);
                    mediaItem1s.add(avMediaItem);

                }

                for (int i = 0; i < 50; i++) {
                    AVMediaItem avMediaItem = new AVMediaItem();
                    avMediaItem.setDescription("xxxx");
                    avMediaItem.setPhoto(new AVFile("photo", ImageUtil.autoCompressImage2byte(path)));
                    avMediaItem.setMediaType(AVMediaItem.MEDIATYPE_ONLY_DESCRIPTION);

                    mediaItemAll.add(avMediaItem);
                    mediaItem2s.add(avMediaItem);
                }

                CuploadThread th1 = new CuploadThread(mediaItem1s);
                CuploadThread th2 = new CuploadThread(mediaItem2s);

                th1.start();
                th2.start();




                while (th1.getCState() == false || th2.getCState() == false) {
                    try {
                        Thread.sleep(500l);
                        Log.i("xx","sleep");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }



                avObject.put("mediaArr",mediaItemAll);
                avObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if(null != e)
                            e.printStackTrace();
                        ToastUtil.toastShort(TestLeanCloudActivity.this, e + "");
                        Log.i("xx", "timeE:" + System.currentTimeMillis());
                    }
                });



            }
        }).start();
    }

    private void saveInSigalThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {



//                11:26:02.356
//                11:28:50.906
//
//                48+120S


//                12:07:56.906
//                12:10:58.986

//                3S

                String path = "/storage/emulated/0/Resource/Coolpad/Image_show/show_01/CoolShow_A000022.jpg";

                Log.i("xx", "timeS:" + System.currentTimeMillis());
                AVObject avObject = new AVObject("t1t");
                List<AVMediaItem> mediaItems = new ArrayList<AVMediaItem>();






                for (int i = 0; i < 100; i++) {
                    AVMediaItem avMediaItem = new AVMediaItem();
                    avMediaItem.setDescription("xxxx");
                    avMediaItem.setPhoto(new AVFile("photo", ImageUtil.autoCompressImage2byte(path)));
                    avMediaItem.setMediaType(AVMediaItem.MEDIATYPE_ONLY_DESCRIPTION);
                    try {
                        avMediaItem.save();
                    } catch (AVException e) {
                        e.printStackTrace();
                    }
                    mediaItems.add(avMediaItem);
                }

                avObject.put("mediaArr",mediaItems);
                avObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if(null != e)
                        e.printStackTrace();
                        ToastUtil.toastShort(TestLeanCloudActivity.this, e + "");
                        Log.i("xx", "timeE:" + System.currentTimeMillis());
                    }
                });
            }
        }).start();
    }


    private void testAddRelation() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                AVObject avObject = new AVObject("t1t");
                //只能get 不能new!!!!! 否则报异常
                AVRelation<AVMediaItem> relation = avObject.getRelation("relation");
                AVMediaItem avMediaItem = new AVMediaItem();
                avMediaItem.setDescription("xxxx");
                avMediaItem.setMediaType(AVMediaItem.MEDIATYPE_ONLY_DESCRIPTION);


                try {
                    avMediaItem.save();
                } catch (AVException e) {
                    e.printStackTrace();
                }

                Log.i("xx",avMediaItem.getObjectId());

                //add下去 必须该AVObj 是有objId的！！！！
                relation.add(avMediaItem);

                avMediaItem = new AVMediaItem();
                avMediaItem.setDescription("bbbb");
                avMediaItem.setMediaType(AVMediaItem.MEDIATYPE_ONLY_DESCRIPTION);

                try {
                    avMediaItem.save();
                } catch (AVException e) {
                    e.printStackTrace();
                }
                Log.i("xx", avMediaItem.getObjectId());
                relation.add(avMediaItem);

//        avObject.put("relation", relation);(由于不能new 这个操作基本作废！直接修改，不要set)
                avObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        ToastUtil.toastLong(TestLeanCloudActivity.this,"xx");
                        if(null != e)
                            e.printStackTrace();
                    }
                });
            }
        }).start();

    }

    private void testAVRelation() {

//        testAddRelation();

         testGetRelation();




    }

    private void testGetRelation() {
        AVQuery<AVObject> avQuery = new AVQuery<AVObject>("t1t");
        avQuery.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (null != e) {
                    ToastUtil.toastLong(TestLeanCloudActivity.this, e.toString());
                    return;
                }

                if (null == list || list.size() <= 0) {
                    ToastUtil.toastLong(TestLeanCloudActivity.this, "空");
                    return;
                }

                AVObject avObject = list.get(0);
                AVRelation<AVMediaItem> avMediaItemAVRelation = avObject.getRelation("relation");
                avMediaItemAVRelation.getQuery().findInBackground(new FindCallback<AVMediaItem>() {
                    @Override
                    public void done(List<AVMediaItem> list, AVException e) {
                        for (int i = 0; i < list.size(); i++) {
                            AVMediaItem avMe = list.get(i);
                            Log.i("xx", avMe.getDescription());
                            Log.i("xx", avMe.getPhoto() + "");
                            if (avMe.getPhoto() != null) {
                                Log.i("xx", avMe.getPhoto().getUrl());
                            }
//                            avMe.deleteInBackground();
                            //                            删除 含有的relation都会自动删除该对象

                        }
                    }
                });



            }
        });
    }

    //    boolean hasPhoto = false;
    final static int SELECT_PHOTO_CODE = 1;
    private void addNativePhoto() {
        Intent intent = new Intent(this, SelectPhotoActivity.class);
        startActivityForResult(intent,SELECT_PHOTO_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == SELECT_PHOTO_CODE)
            onSelectPhontoResult(resultCode,data);



    }

    int count = 0;
    private void onSelectPhontoResult(int resultCode, Intent data) {
        if (resultCode != RESULT_OK || data == null)
            return;
        String[] photoPaths = data.getStringArrayExtra("photoPaths");
        if(null == photoPaths || photoPaths.length <= 0)
            return;
        this.photoPath = photoPaths[0];
        Log.i("xx",this.photoPath);
        this.photoBitmap = getBitMap(this.photoPath);
        nativePhotoImageView.setImageBitmap(photoBitmap);

        setHasPhotoState();


    }

    private void setHasPhotoState(){
        if(!this.hasPhoto) {
            this.hasPhoto = true;

            File file = new File(this.photoPath);

            Log.i("xx",System.currentTimeMillis()+":start  1");
                 AVFile avFile =  new AVFile(file.getName(), ImageUtil.autoCompressImage2byte(this.photoPath));

                    avFile.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            getNetPhotoBtn.setEnabled(true);

                                Log.i("xx", System.currentTimeMillis() + ":finish   3");

                        }
                    });
            Log.i("xx", System.currentTimeMillis() + ":yasuo finsih  2");
                      this.avFile = avFile;

        }


    }

    private void setNoPhotoState(){
        if(this.hasPhoto) {
            this.hasPhoto = false;
            getNetPhotoBtn.setEnabled(false);
        }
    }


    private Bitmap getBitMap(String photoPath) {
        return BitmapFactory.decodeFile(photoPath);
    }

    ProgressDialog progressDialog;
    private void getNetPhoto() {
        if (!hasPhoto)
            return;
                progressDialog = ProgressDialog.show(this,null,null);
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                try {
                    url = new URL(avFile.getUrl());
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    Bitmap myBitmap = BitmapFactory.decodeStream(input);
                    Message msg = new Message();
                    msg.obj = myBitmap;
                    handler.sendMessage(msg);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }).start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            netPhotoImageView.setImageBitmap((Bitmap) msg.obj);
            progressDialog.dismiss();
        }
    };

*/

}
