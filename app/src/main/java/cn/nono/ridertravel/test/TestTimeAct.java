package cn.nono.ridertravel.test;

import android.app.Dialog;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import cn.nono.ridertravel.R;
import cn.nono.ridertravel.debug.ToastUtil;
import cn.nono.ridertravel.ui.base.BaseNoTitleActivity;

/**
 * Created by Administrator on 2016/4/18.
 */
public class TestTimeAct extends BaseNoTitleActivity implements View.OnClickListener{

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_ll);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        Dialog dialog = new Dialog(this);
        dialog.setCancelable(false);
        dialog.setTitle("xxxxxxx");
        Window window = dialog.getWindow();
        window.setTitle("abc");
           window.requestFeature(Window.FEATURE_NO_TITLE);
        window.setContentView(R.layout.dialogt);
        window.setGravity(Gravity.BOTTOM);
        window.getDecorView().setPadding(0,0,0,0);
       WindowManager windowManager = window.getWindowManager();
       WindowManager.LayoutParams lp = window.getAttributes();
        Point point = new Point();
        windowManager.getDefaultDisplay().getSize(point);
        lp.width = point.x;
        ToastUtil.toastLong(this,"x:"+point.x+" y:"+point.y);



        dialog.show();
    }
}
