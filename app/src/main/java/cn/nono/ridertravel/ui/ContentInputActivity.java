package cn.nono.ridertravel.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import cn.nono.ridertravel.R;
import cn.nono.ridertravel.debug.ToastUtil;
import cn.nono.ridertravel.ui.base.BaseNoTitleActivity;
import cn.nono.ridertravel.util.DataSimpleGetUtil;

/**
 * Created by Administrator on 2016/4/18.
 */
public class ContentInputActivity extends BaseNoTitleActivity implements View.OnClickListener{

    public final static String TITLE_KEY = "titile";
    public final static String RES_CONTENT_KEY = "content";
    public final static String CONTENT_DEFAULT_KEY = "defaultStr";

    TextView titleTextView;
    Button finishButton;
    EditText contenInputEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_input);
        initView();
        Intent intent = getIntent();
        String title = intent.getStringExtra(TITLE_KEY);
        if(null == title || title.isEmpty()) {
            finish();
            return;
        }
        titleTextView.setText(title);
        String defStr = intent.getStringExtra(CONTENT_DEFAULT_KEY);
        if(null != defStr && defStr.isEmpty()) {
            contenInputEditText.setText(defStr);
        }
    }

    private void initView() {
        titleTextView = (TextView) findViewById(R.id.title_tv);
        finishButton = (Button) findViewById(R.id.finish_btn);
        finishButton.setOnClickListener(this);
        contenInputEditText = (EditText) findViewById(R.id.content_input_et);
    }

    @Override
    public void onClick(View v) {
        String content = DataSimpleGetUtil.getEditTextStr(contenInputEditText);
        if(content.isEmpty()) {
            ToastUtil.toastLong(this,"没有输入内容!请重新输入。");
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(RES_CONTENT_KEY,content);
        setResult(RESULT_OK,intent);
        finish();
    }
}
