package io.passmaster.Passmaster;

import java.lang.ref.WeakReference;
import android.app.Activity;
import android.content.Intent;
import android.net.MailTo;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class PassmasterWebViewClient extends WebViewClient {

  private final WeakReference<Activity> activityRef;
  private final String passmasterErrorHTML =
      "<html>" +
      "<head>" +
        "<style type='text/css'>" +
          "body { background-color: #8b99ab; color: #fff; text-align: center; font-family: arial, sans-serif; }" +
        "</style>" +
      "</head>" +
      "<body>" +
        "<div>" +
          "<h2>Passmaster</h2>" +
          "<h4>We're sorry, but something went wrong.</h4>" +
          "<h4>%s</h4>" +
        "</div>" +
      "</body>" +
      "</html>";

  public PassmasterWebViewClient(Activity activity) {
    activityRef = new WeakReference<Activity>(activity);
  }

  @Override
  public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
    String errorString = String.format(passmasterErrorHTML, description);
    view.loadData(errorString, "text/html", null);
  }

  @Override
  public boolean shouldOverrideUrlLoading(WebView view, String url) {
    if (url.startsWith("mailto:")) {
      final Activity activity = activityRef.get();
      if (activity != null) {
        MailTo mailTo = MailTo.parse(url);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] { mailTo.getTo() });
        intent.setType("message/rfc822");
        activity.startActivity(intent);
        return true;
      }
    } else if (url.startsWith("http") && !url.startsWith(PassmasterActivity.PASSMASTER_URL)) {
      final Activity activity = activityRef.get();
      if (activity != null) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        activity.startActivity(intent);
        return true;
      }
    }
    return false;
  }

}
