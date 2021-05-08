package com.spark.xposeddy.component.prompt;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.spark.xposeddy.R;

/**
 * 提示框
 */
public class PromptDialog extends Dialog {

    public PromptDialog(Context context) {
        super(context);
    }

    public PromptDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected PromptDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public static class Builder {
        private Activity mContext; // 上下文对象
        private int mLayout = -1;     // 对话框布局
        private String mTitle;  // 对话框标题
        private String mContent; // 对话框内容
        private String mPrompt;  // 中性提醒按键
        private String mCancel;  // 取消按键
        private String mConfirm; // 确定按键
        private DialogInterface.OnClickListener mClickListener = null;

        public Builder(Activity context) {
            this.mContext = context;
        }

        public Builder setLayout(int layout) {
            mLayout = layout;
            return this;
        }

        public Builder setTitle(String title) {
            mTitle = title;
            return this;
        }

        public Builder setContent(String mContent) {
            this.mContent = mContent;
            return this;
        }

        public Builder setSingleBtn(String prompt, DialogInterface.OnClickListener listener) {
            this.mPrompt = prompt;
            this.mCancel = null;
            this.mConfirm = null;
            this.mClickListener = listener;
            return this;
        }

        public Builder setDoubleBtn(String confirm, String cancel, DialogInterface.OnClickListener listener) {
            this.mPrompt = null;
            this.mConfirm = confirm;
            this.mCancel = cancel;
            this.mClickListener = listener;
            return this;
        }

        public Builder setThreeBtn(String confirm, String prompt, String cancel, DialogInterface.OnClickListener listener) {
            this.mPrompt = prompt;
            this.mConfirm = confirm;
            this.mCancel = cancel;
            this.mClickListener = listener;
            return this;
        }

        public PromptDialog create() {
            final PromptDialog dialog = new PromptDialog(mContext, R.style.dialog);
            View view = mLayout == -1 ? View.inflate(mContext, R.layout.dialog_prompt_default, null) : View.inflate(mContext, mLayout, null);
            dialog.setContentView(view);
            TextView title = ((TextView) dialog.findViewById(R.id.title));
            TextView context = ((TextView) dialog.findViewById(R.id.content));
            TextView prompt = ((TextView) dialog.findViewById(R.id.prompt));
            TextView confirm = ((TextView) dialog.findViewById(R.id.confirm));
            TextView cancel = ((TextView) dialog.findViewById(R.id.cancel));

            if (!TextUtils.isEmpty(mTitle)) {
                title.setText(mTitle);
            } else {
                title.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(mContent)) {
                context.setText(mContent);
            }

            if (!TextUtils.isEmpty(mPrompt) && TextUtils.isEmpty(mConfirm) && TextUtils.isEmpty(mCancel)) {
                prompt.setVisibility(View.VISIBLE);
                confirm.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);
                prompt.setText(mPrompt);
            } else if (TextUtils.isEmpty(mPrompt) && !TextUtils.isEmpty(mConfirm) && !TextUtils.isEmpty(mCancel)) {
                prompt.setVisibility(View.GONE);
                confirm.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.VISIBLE);
                confirm.setText(mConfirm);
                cancel.setText(mCancel);
            } else if (!TextUtils.isEmpty(mPrompt) && !TextUtils.isEmpty(mConfirm) && !TextUtils.isEmpty(mCancel)) {
                prompt.setVisibility(View.VISIBLE);
                confirm.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.VISIBLE);
                prompt.setText(mPrompt);
                confirm.setText(mConfirm);
                cancel.setText(mCancel);
            }

            prompt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener != null) {
                        mClickListener.onClick(dialog, DialogInterface.BUTTON_NEUTRAL);
                    }
                    dialog.dismiss();
                }
            });

            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener != null) {
                        mClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                    }
                    dialog.dismiss();
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener != null) {
                        mClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
                    }
                    dialog.dismiss();
                }
            });


            Window window = dialog.getWindow();
            window.setGravity(Gravity.CENTER);
            WindowManager windowManager = mContext.getWindowManager();
            Display display = windowManager.getDefaultDisplay();
            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            lp.width = (int) (display.getWidth());
            dialog.getWindow().setAttributes(lp);

            return dialog;
        }
    }
}
