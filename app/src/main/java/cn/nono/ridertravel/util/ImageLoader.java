package cn.nono.ridertravel.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import cn.nono.ridertravel.R;

public class ImageLoader {


	private static final int LOAD_TASK_MSG_WHAT = 1;
	private static final int TASK_FINISH_REFRESH_UI_MSG_WHAT = 2;

	private static ImageLoader mImageLoaderInstance = null;
	/**
	 * Bitmap 缓存类
	 */
	private LruCache<String, Bitmap> mBitmapLruCache = null;
	/**
	 * 执行线程池
	 */
	private ExecutorService mExecutorThreadPool = null;
	private int mThreadCount;
	//默认线程数量
//	private final static int DEFAULT_THREAD_COUNT = 4;
	/**
	 * 信号量
	 */
	private Semaphore mSemaphore;
	/**
	 * 任务队列
	 */
	private LinkedList<Runnable> mTaskQueue = null;

	/**
	 * 任务(队列)执行策略
	 * @author nono
	 *
	 */
	enum TaskExecutStrategy {
		//先进先出
		FIFO,
		//后进先出
		LIFO
	}

	/**
	 * 任务(队列)执行策略
	 */
	private TaskExecutStrategy mTaskExecutStrategy;
	/**
	 * 任务队列分发 (任务队列轮训) 线程
	 */
	private Thread  mTaskDispatchRotationThread;
	/**
	 * 任务分发线程的消息handler
	 */
	private Handler mTaskDispatchRotationThreadHandler;
	private Semaphore mThreadHandlerSemaphore;
	/**
	 * ui刷新handler
	 */
	private Handler mUIHandler;


	private ImageLoader() {
		//初始化缓存
		int runTimeMaxMemory =  (int) Runtime.getRuntime().maxMemory();
		int maxMemory = runTimeMaxMemory >> 3;//runTimeMaxMemory / 8
		mBitmapLruCache = new LruCache<String, Bitmap> (maxMemory) {

			@Override
			protected int sizeOf(String key, Bitmap value) {
				// TODO Auto-generated method stub
				return value.getRowBytes() * value.getHeight();
			}

		};

		//初始化线程池
		mThreadCount = 4;
		mExecutorThreadPool = Executors.newFixedThreadPool(mThreadCount);

		//初始化uiHandler
		mUIHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				//刷新ui
				ImageHolder imageHolder = (ImageHolder) msg.obj;
				ImageView imageView = imageHolder.ImageView;
				String imageTag = (String) imageView.getTag();
				//防止图片乱序（在正确的位置上显示正确的图片）
				if(imageHolder.path.equals(imageTag)) {
					//bitmap 可能为空！！！
					if(null != imageHolder.bitmap)
						imageView.setImageBitmap(imageHolder.bitmap);
				}
			}


		};


		//初始化任务队列
		mTaskQueue = new LinkedList<Runnable>();
		//初始化任务队列执行策略
		mTaskExecutStrategy = TaskExecutStrategy.LIFO;
		//初始化 任务轮训线程
		mSemaphore = new Semaphore(mThreadCount);
		mThreadHandlerSemaphore = new Semaphore(0);
		mTaskDispatchRotationThread = new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Looper.prepare();
				mTaskDispatchRotationThreadHandler = new Handler() {

					@Override
					public void handleMessage(Message msg) {
						// TODO Auto-generated method stub
						//分发任务
						try {
							//获取信号量
							mSemaphore.acquire();
							Runnable taskRunnable = getTask();
							//执行任务
							mExecutorThreadPool.execute(taskRunnable);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (NoSuchElementException e2) {
							//任务队列为空 获取任务失败。释放之前获取的信号量
							mSemaphore.release();
						}

					}
				};
				mThreadHandlerSemaphore.release();
				Looper.loop();
			}
		};
		mTaskDispatchRotationThread.start();

	}

	private Runnable getTask() {
		// TODO Auto-generated method stub
		Runnable taskRunnable = null;
		if(TaskExecutStrategy.LIFO == mTaskExecutStrategy) {
			taskRunnable = mTaskQueue.removeLast();
		}
		//FIFO
		else {
			taskRunnable = mTaskQueue.removeFirst();
		}

		return taskRunnable;
	}

	public static ImageLoader getInstance() {
		if (null != mImageLoaderInstance) {
			return mImageLoaderInstance;
		}
		synchronized (ImageLoader.class) {
			if (null != mImageLoaderInstance)
				return mImageLoaderInstance;
			mImageLoaderInstance = new ImageLoader();
			return mImageLoaderInstance;
		}
	}

	/**
	 * 加载image
	 * @param path
	 * @param imageView
	 */
	public synchronized void loadImage(String path,ImageView imageView) {

		TaskRunnable task = new TaskRunnable(path, imageView);

		imageView.setTag(path);
		imageView.setImageResource(R.mipmap.ic_launcher);

		mTaskQueue.add(task);
		try {
			if(null == mTaskDispatchRotationThreadHandler)
				mThreadHandlerSemaphore.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Message message = mTaskDispatchRotationThreadHandler.obtainMessage();
		message.what = LOAD_TASK_MSG_WHAT;
		mTaskDispatchRotationThreadHandler.sendMessage(message);
	}

	class TaskRunnable implements Runnable {

		private String path;
		private ImageView imageView;

		public TaskRunnable(String path, android.widget.ImageView imageView) {
			super();
			this.path = path;
			this.imageView = imageView;
		}

		public String getPath() {
			return path;
		}

		public ImageView getImageView() {
			return imageView;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub

			//缓存图片
			Bitmap bitmap = mBitmapLruCache.get(path);
			//没有图片缓存
			if(null == bitmap) {
				imageViewSize viewSize = getImageViewSize(imageView);
				BitmapFactory.Options options = new BitmapFactory.Options();
				//不把图片加载在内存，但是图片的其他字段（其他信息依然能读取）
				options.inJustDecodeBounds = true;
				//加载文件.
				BitmapFactory.decodeFile(path, options);

				options.inSampleSize = caculateInSampleSize(options,viewSize.with,viewSize.height);
				//使用获取到的SampleSize （压缩比） 重新设置 把文件加载在内存
				options.inJustDecodeBounds = false;
				bitmap = BitmapFactory.decodeFile(path, options);
				//注意这里可能出现无法解析图片的情况
				if(bitmap != null)
					mBitmapLruCache.put(path, bitmap);
				else {
					//可自定义加载无效图片的对应的bitmap
//					bitmap = getDefaultDestroyedBitmap();
//					mBitmapLruCache.put(path, bitmap);
				}
			}

			Message message = mUIHandler.obtainMessage();
			message.what = TASK_FINISH_REFRESH_UI_MSG_WHAT;
			ImageHolder imageHolder = new ImageHolder();
			imageHolder.ImageView = imageView;
			imageHolder.path = path;
			imageHolder.bitmap = bitmap;
			message.obj = imageHolder;
			mUIHandler.sendMessage(message);
			//任务结束释放信号量
			mSemaphore.release();
		}

	}



	class ImageHolder {
		public Bitmap bitmap;
		public String path;
		public ImageView ImageView;
	}

	class imageViewSize {
		public int with;
		public int height;
	}

	/**
	 * 获取imageView 的大小。（这个函数获取的大小是有BUG的，待完善）
	 * @param imageView
	 * @return
	 */
	private imageViewSize getImageViewSize(ImageView imageView) {
		// TODO Auto-generated method stub

		DisplayMetrics metrics = imageView.getContext().getResources().getDisplayMetrics();
		//图片压缩(压缩大小尽可能贴近ImageView 的设定的大小(很可惜，不一定能获取其大小)) 然后放入缓存
		int width = imageView.getWidth();
		//布局文件大小
		if(width <= 0) {
			width =	imageView.getLayoutParams().width;
		}

		//直接获取(不一定有)
		if(width <= 0) {
			//获取imageView布局文件设置的参数
			width = imageView.getMaxWidth();
		}

		if(width <= 0) {
			//最坏这里了，只能获取屏幕的大小（也比你高清照片来的小）了
			width = metrics.widthPixels;
		}

		//图片压缩(压缩大小尽可能贴近ImageView 的设定的大小(很可惜，不一定能获取其大小)) 然后放入缓存
		int height = imageView.getHeight();
		if(height <= 0) {
			height = imageView.getLayoutParams().height;
		}
		//直接获取(不一定有)
		if(height <= 0) {
			//获取imageView布局文件设置的参数
			height = imageView.getMaxHeight();
		}

		if(height <= 0) {
			//最坏这里了，只能获取屏幕的大小（也比你高清照片来的小）了
			height = metrics.heightPixels;
		}

		imageViewSize size = new imageViewSize();
		size.with = width;
		size.height = height;

//		size.with = 200;
//		size.height = 200;
		return size;
	}

	/**
	 * 根据需求的宽和高以及图片实际的宽和高计算SampleSize
	 * @param options
	 * @param reqWith
	 * @param reqHeight
	 * @return
	 */
	private int caculateInSampleSize(Options options, int reqWith, int reqHeight) {
		// TODO Auto-generated method stub
		int with = options.outWidth;
		int height = options.outHeight;
		int sampleSize = 1;
		if(with > reqWith || height > reqHeight) {
			int withRadio = Math.round(with*1.0f/reqWith);
			int heightRadio = Math.round(height*1.0f/reqHeight);
			sampleSize = Math.max(withRadio, heightRadio);
		}
		return sampleSize;
	}

}
