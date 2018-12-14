package com.mwj.messagecode.messagecode;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;


/**
 * 用于数字输入View
 * Created by Jimes
 */

public class MessageCodeLayout extends LinearLayout {

    private int maxLength = 6; //密码长度

    private int inputIndex = 0; //设置子View状态index

    private List<String> mPassList;//储存密码

    private MessageCodeChangeListener messageCodeChangeListener;//密码状态改变监听


    private Context mContext;

    private int mInputColor;
    private int mNoinputColor;
    private int minputedColor;
    private int mTextColor;
    private int mInterval;
    private int mItemWidth;
    private int mItemHeight;
    private int mTxtSize;
    private int mBoxLineSize;


    public void setMessageCodeChangeListener(MessageCodeChangeListener messageCodeChangeListener) {
        this.messageCodeChangeListener = messageCodeChangeListener;
    }

    public MessageCodeLayout(Context context) {
        this(context, null);
    }

    public MessageCodeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MessageCodeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    /**
     * 初始化View
     */
    private void initView(Context context, AttributeSet attrs) {

        mContext = context;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MessageCodeLayout);

        mInputColor = ta.getResourceId(R.styleable.MessageCodeLayout_box_input_color, R.color.light_white);
        mNoinputColor = ta.getResourceId(R.styleable.MessageCodeLayout_box_no_input_color, R.color.light_white);
        minputedColor = ta.getResourceId(R.styleable.MessageCodeLayout_box_inputed_color, R.color.transparent);
        mTextColor = ta.getColor(R.styleable.MessageCodeLayout_text_color, Color.BLACK);

        mInterval = ta.getDimensionPixelOffset(R.styleable.MessageCodeLayout_interval_width, 4);
        maxLength = ta.getInt(R.styleable.MessageCodeLayout_pass_leng, 6);
        mItemWidth = ta.getDimensionPixelOffset(R.styleable.MessageCodeLayout_item_width, 40);
        mItemHeight = ta.getDimensionPixelOffset(R.styleable.MessageCodeLayout_item_height, 40);

        mTxtSize = ta.getDimensionPixelOffset(R.styleable.MessageCodeLayout_draw_txt_size, 18);
        mBoxLineSize = ta.getDimensionPixelOffset(R.styleable.MessageCodeLayout_draw_box_line_size, 4);
        ta.recycle();

        mPassList = new ArrayList<>();

        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);

        //设置点击时弹出输入法
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setFocusable(true);
                setFocusableInTouchMode(true);
                requestFocus();
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(MessageCodeLayout.this, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        this.setOnKeyListener(new MyKeyListener());//按键监听

        setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    MessageCodeView messageCodeView = (MessageCodeView) getChildAt(inputIndex);
                    if (messageCodeView != null) {
                        messageCodeView.startInputState();
                    }
                } else {
                    MessageCodeView messageCodeView = (MessageCodeView) getChildAt(inputIndex);
                    if (messageCodeView != null) {
                        messageCodeView.updateInputState(false);
                    }
                }
            }
        });
    }

    /**
     * 添加子View
     *
     * @param context
     */
    private void addChildVIews(Context context) {
        for (int i = 0; i < maxLength; i++) {
            MessageCodeView messageCodeView = new MessageCodeView(context);
            LayoutParams params = new LayoutParams(mItemWidth, mItemHeight);
            if (i > 0) {                                       //第一个和最后一个子View不添加边距
                params.leftMargin = mInterval;
            }

            messageCodeView.setInputStateColor(mInputColor);
            messageCodeView.setNoinputColor(mNoinputColor);
            messageCodeView.setInputedStateBoxColor(minputedColor);
            messageCodeView.setTextColor(mTextColor);
            messageCodeView.setmDrawTxtSize(mTxtSize);
            messageCodeView.setmDrawBoxLineSize(mBoxLineSize);

            addView(messageCodeView, params);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (getChildCount() == 0) {     //判断 子View宽+边距是否超过了父布局 超过了则重置宽高
//            if ((maxLength * mItemWidth + (maxLength - 1) * mInterval) > getMeasuredWidth()) {
//                mItemWidth = (getMeasuredWidth() - (maxLength - 1) * mInterval) / maxLength;
//                mItemHeight = mItemWidth;
//            }

            addChildVIews(getContext());
        }

    }

    /**
     * 单个添加
     *
     * @param msgCode
     */
    public void addCode(String msgCode) {
        if (mPassList != null && mPassList.size() < maxLength) {
            mPassList.add(msgCode + "");
            setNextInput(msgCode);
        }

        if (messageCodeChangeListener != null) {
            if (mPassList.size() < maxLength) {
                messageCodeChangeListener.onChange(getCodeString());
            } else {
                messageCodeChangeListener.onFinished(getCodeString());
            }
        }
    }

    /**
     * 添加字符串
     *
     * @param msgCode
     */
    public void addCodes(String msgCode) {

        if (TextUtils.isEmpty(msgCode)) return;

        for (int i = 0; i < msgCode.length(); i++) {
            mPassList.add(msgCode.charAt(i) + "");
            setNextInput(String.valueOf(msgCode.charAt(i)));
        }

        if (messageCodeChangeListener != null) {
            if (mPassList.size() < maxLength) {
                messageCodeChangeListener.onChange(getCodeString());
            } else {
                messageCodeChangeListener.onFinished(getCodeString());
            }
        }
    }


    /**
     * 删除
     */
    public void removeCode() {
        if (mPassList != null && mPassList.size() > 0) {
            mPassList.remove(mPassList.size() - 1);
            setPreviosInput();
        }

        if (messageCodeChangeListener != null) {
            if (mPassList.size() > 0) {
                messageCodeChangeListener.onChange(getCodeString());
            } else {
                messageCodeChangeListener.onNull();
            }
        }
    }

    /**
     * 清空所有
     */
    public void removeAllCode() {
        if (mPassList != null) {
            for (int i = mPassList.size(); i >= 0; i--) {
                if (i > 0) {
                    setNoInput(i, false, "");
                } else if (i == 0) {
                    MessageCodeView messageCodeView = (MessageCodeView) getChildAt(i);
                    if (messageCodeView != null) {
                        messageCodeView.setmPassText("");
                        messageCodeView.startInputState();
                    }
                }

            }

            mPassList.clear();
            inputIndex = 0;
        }


        if (messageCodeChangeListener != null) {
            messageCodeChangeListener.onNull();
        }
    }

    /**
     * 获取
     *
     * @return pwd
     */
    public String getCodeString() {

        StringBuffer passString = new StringBuffer();

        for (String i : mPassList) {
            passString.append(i);
        }

        return passString.toString();
    }

    /**
     * 设置下一个View为输入状态
     */
    private void setNextInput(String pwdTxt) {
        if (inputIndex < maxLength) {
            setNoInput(inputIndex, true, pwdTxt);
            inputIndex++;
            MessageCodeView messageCodeView = (MessageCodeView) getChildAt(inputIndex);
            if (messageCodeView != null) {
                messageCodeView.setmPassText(pwdTxt + "");
                messageCodeView.startInputState();
            }
        }

    }

    /**
     * 设置上一个View为输入状态
     */
    private void setPreviosInput() {
        if (inputIndex > 0) {
            setNoInput(inputIndex, false, "");
            inputIndex--;
            MessageCodeView messageCodeView = (MessageCodeView) getChildAt(inputIndex);
            if (messageCodeView != null) {
                messageCodeView.setmPassText("");
                messageCodeView.startInputState();
            }
        } else if (inputIndex == 0) {
            MessageCodeView messageCodeView = (MessageCodeView) getChildAt(inputIndex);
            if (messageCodeView != null) {
                messageCodeView.setmPassText("");
                messageCodeView.startInputState();
            }
        }
    }

    /**
     * 设置指定View为不输入状态
     *
     * @param index   view下标
     * @param isinput 是否输入过密码
     */
    public void setNoInput(int index, boolean isinput, String txt) {
        if (index < 0) {
            return;
        }
        MessageCodeView messageCodeView = (MessageCodeView) getChildAt(index);
        if (messageCodeView != null) {
            messageCodeView.setmPassText(txt);
            messageCodeView.updateInputState(isinput);
        }
    }


    public interface MessageCodeChangeListener {
        void onChange(String pwd);//改变

        void onNull();  //删除为空

        void onFinished(String code);//长度已经达到最大值
    }


    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        outAttrs.inputType = InputType.TYPE_CLASS_NUMBER;          //显示数字键盘
        outAttrs.imeOptions = EditorInfo.IME_FLAG_NO_EXTRACT_UI;
        return new ZanyInputConnection(this, false);
    }


    private class ZanyInputConnection extends BaseInputConnection {

        @Override
        public boolean commitText(CharSequence txt, int newCursorPosition) {
            return super.commitText(txt, newCursorPosition);
        }

        public ZanyInputConnection(View targetView, boolean fullEditor) {
            super(targetView, fullEditor);
        }

        @Override
        public boolean sendKeyEvent(KeyEvent event) {
            return super.sendKeyEvent(event);
        }


        @Override
        public boolean deleteSurroundingText(int beforeLength, int afterLength) {
            if (beforeLength == 1 && afterLength == 0) {
                return sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL)) && sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
            }

            return super.deleteSurroundingText(beforeLength, afterLength);
        }
    }


    /**
     * 按键监听器
     */
    class MyKeyListener implements OnKeyListener {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (event.isShiftPressed()) {//处理*#等键
                    return false;
                }
                if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {//处理数字
                    addCode(keyCode - 7 + "");              //点击添加密码
                    return true;
                }

                if (keyCode == KeyEvent.KEYCODE_DEL) {       //点击删除
                    removeCode();
                    return true;
                }

                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                return true;
            }
            return false;
        }//onKey
    }


    //恢复状态
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        this.mPassList = savedState.saveString;
        inputIndex = mPassList.size();
        if (mPassList.isEmpty()) {
            return;
        }
        for (int i = 0; i < getChildCount(); i++) {
            MessageCodeView messageCodeView = (MessageCodeView) getChildAt(i);
            if (i > mPassList.size() - 1) {
                if (messageCodeView != null) {
                    messageCodeView.updateInputState(false);
                }
                break;
            }

            if (messageCodeView != null) {
                messageCodeView.setmPassText(mPassList.get(i));
                messageCodeView.updateInputState(true);
            }
        }

    }

    //保存状态
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.saveString = this.mPassList;
        return savedState;
    }


    public static class SavedState extends BaseSavedState {
        public List<String> saveString;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);

            dest.writeList(saveString);
        }

        private SavedState(Parcel in) {
            super(in);
            in.readStringList(saveString);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }


}
