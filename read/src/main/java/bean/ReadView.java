package bean;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.util.Log;
import android.widget.TextView;

public class ReadView extends TextView {

    public ReadView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        resize();
    }

    /**
     * 去除当前页无法显示的字
     *
     * @return 去掉的字数
     */
    public int resize() {
        CharSequence oldContent = getText();
        CharSequence newContent = oldContent.subSequence(0, getCharNum());
        setText(newContent);
        return oldContent.length() - newContent.length();
    }

    /**
     * 获取当前页总字数
     */
    public int getCharNum() {
        return getLayout().getLineEnd(getLineNum());
    }

    /**
     * 获取当前页总行数
     */
    @Nullable
    public int getLineNum() {
        Layout layout = getLayout();
        int topOfLastLine = getHeight() - getPaddingTop() - getPaddingBottom()
                - getLineHeight();
        Log.i("toll" , topOfLastLine + "");
            return layout.getLineForVertical(topOfLastLine);
    }
}
