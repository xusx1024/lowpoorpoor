TextView 拦截超链接，不再进入系统浏览器，响应自定义点击。需求，服务端返回html标签文本，android显示，并可以响应点击。



```
// 解析TextView中的url，为止设置自定义的点击事件。
public static void interceptHyperLink(TextView tv, String title) {
    tv.setMovementMethod(LinkMovementMethod.getInstance());
    CharSequence text = tv.getText();
    if (text instanceof Spannable) {
      int end = text.length();
      Spannable spannable = (Spannable) tv.getText();
      URLSpan[] urlSpans = spannable.getSpans(0, end, URLSpan.class);
      if (urlSpans.length == 0) {
        return;
      }
      SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
      spannableStringBuilder.clearSpans();
      for (URLSpan uri : urlSpans) {
        String url = uri.getURL();
        CustomUrlSpan customUrlSpan = new CustomUrlSpan(tv.getContext(), url, title);
        spannableStringBuilder.setSpan(customUrlSpan, spannable.getSpanStart(uri),
            spannable.getSpanEnd(uri), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
      }
      tv.setText(spannableStringBuilder);
    }
  }
```

```
// 自定义点击，一般是进入自己的应用内webview
public class CustomUrlSpan extends ClickableSpan {
  private Context mContext;
  private String url;
  private String title;

  public CustomUrlSpan(Context context, String url, String title) {
    mContext = context;
    this.url = url;
    this.title = title;
  }

  @Override public void onClick(@NonNull View widget) {
    BrowserActivity.start(mContext, url, title);
  }
}
```

