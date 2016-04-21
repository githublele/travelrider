package cn.nono.ridertravel.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import cn.nono.ridertravel.R;
import cn.nono.ridertravel.bean.PhotoBean;
import cn.nono.ridertravel.debug.ToastUtil;
import cn.nono.ridertravel.ui.base.BaseNoTitleActivity;
import cn.nono.ridertravel.util.DataSimpleGetUtil;
import cn.nono.ridertravel.util.ImageLoader;

public class AddPhotoDescriptionActivity extends BaseNoTitleActivity implements OnClickListener{

	PhotoBean photo = null;
	TextView canelTextView;
	TextView okTextView;
	EditText photoDescriptionEditText;
	ImageView photoImageView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_photo_description);
		photo = (PhotoBean) getIntent().getParcelableExtra("photo");
		if(null == photo) {
			finish();
			return;
		}

		canelTextView = (TextView) findViewById(R.id.cancel_tv);
		canelTextView.setOnClickListener(this);
		okTextView = (TextView) findViewById(R.id.ok_tv);
		okTextView.setOnClickListener(this);
		setResult(RESULT_CANCELED);
		photoDescriptionEditText = (EditText) findViewById(R.id.photo_description_et);
		photoDescriptionEditText.setText(photo.description);
		photoImageView = (ImageView) findViewById(R.id.photo_iv);
		ImageLoader.getInstance().loadImage(photo.path, photoImageView);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int viewId = v.getId();
		if(R.id.cancel_tv == viewId) {
			finish();
			return;
		}

		if(R.id.ok_tv == viewId) {
			String photoDescription = DataSimpleGetUtil.getEditTextData(photoDescriptionEditText);
			if(null == photoDescription) {
				ToastUtil.toastShort(this, "输入空");
				return;
			}
			photo.description = photoDescription;
			Intent data = new Intent();
			data.putExtra("photo",photo);
			setResult(RESULT_OK, data);
			finish();
		}

	}
}
