package pl.elfdump.wloczykij.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import pl.elfdump.wloczykij.R;

public class ButtonAction extends LinearLayout {

    private boolean showText;
    private String text;
    private int iconSize;
    private int textColor;

    private Drawable drawable;

    private TextView textView;
    private ImageView imageView;

    private Animation bounce;

    public ButtonAction(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ButtonAction,
                0, 0);

        showText = a.getBoolean(R.styleable.ButtonAction_showLabel, true);
        text = a.getString(R.styleable.ButtonAction_android_text);
        textColor = a.getInt(R.styleable.ButtonAction_android_textColor, ContextCompat.getColor(context, R.color.white));

        iconSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                a.getInt(R.styleable.ButtonAction_iconSize, 28),
                getResources().getDisplayMetrics());

        drawable = a.getDrawable(R.styleable.ButtonAction_src);
        a.recycle();

        LayoutInflater.from(context).inflate(R.layout.place_details_header_button, this);

        textView = (TextView) findViewById(R.id.button_action_label);
        textView.setText(text);
        textView.setTextColor(textColor);

        if(!showText){
            textView.setVisibility(GONE);
        }

        imageView = (ImageView) findViewById(R.id.button_action_icon);
        imageView.setImageDrawable(drawable);

        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.width = iconSize;
        layoutParams.height = iconSize;
        imageView.setLayoutParams(layoutParams);

        bounce = AnimationUtils.loadAnimation(context, R.anim.bounce);

    }

    public String getText(){
        return text;
    }

    public Drawable getDrawable(){
        return drawable;
    }

    public ImageView getImageView(){
        return imageView;
    }

    public void startBounceAnimation(){
        imageView.startAnimation(bounce);
    }

    public void setText(String text) {
        this.text = text;
        textView.setText(text);
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
        imageView.setImageDrawable(drawable);
    }
}
