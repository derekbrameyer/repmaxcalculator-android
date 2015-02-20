package com.doomonafireball.repmaxcalculator.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.doomonafireball.repmaxcalculator.R;
import com.doomonafireball.repmaxcalculator.util.IabHelper;
import com.doomonafireball.repmaxcalculator.util.IabResult;
import com.doomonafireball.repmaxcalculator.util.Inventory;
import com.doomonafireball.repmaxcalculator.util.Purchase;
import com.willowtreeapps.saguaro.android.Saguaro;

/**
 * Created by derek on 11/18/14.
 */
public class AboutActivity extends ActionBarActivity {

    private static final int REQUEST_CODE_IAB = 10001;
    private static final String SKU_DOLLAR_1 = "dollar1x";
    private static final String SKU_DOLLAR_2 = "dollar2x";
    private static final String SKU_DOLLAR_5 = "dollar5x";
    private static final String SKU_DOLLAR_10 = "dollar10x";

    IabHelper mIabHelper;
    private boolean mIsIabSetup = false;

    private TextView aboutTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        aboutTextView = (TextView) findViewById(R.id.about_text);
        aboutTextView.append("\n" + Saguaro.getFullVersionString(this));

        String base64EncodedPublicKey = getString(R.string.base_64_encoded_key);
        mIabHelper = new IabHelper(this, base64EncodedPublicKey);

        mIabHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    mIsIabSetup = false;
                } else {
                    mIsIabSetup = true;
                }
                supportInvalidateOptionsMenu();

                // Have we been disposed of in the meantime? If so, quit.
                if (mIabHelper == null) return;

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                mIabHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button developerPlayground = (Button) findViewById(R.id.developer_playground);
        developerPlayground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AboutActivity.this, DeveloperPlaygroundActivity.class));
            }
        });
    }

    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            // Have we been disposed of in the meantime? If so, quit.
            if (mIabHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                return;
            }

            Purchase dollar1Purchase = inventory.getPurchase(SKU_DOLLAR_1);
            if (dollar1Purchase != null) {
                mIabHelper.consumeAsync(inventory.getPurchase(SKU_DOLLAR_1), mConsumeFinishedListener);
                return;
            }

            Purchase dollar2Purchase = inventory.getPurchase(SKU_DOLLAR_2);
            if (dollar2Purchase != null) {
                mIabHelper.consumeAsync(inventory.getPurchase(SKU_DOLLAR_2), mConsumeFinishedListener);
                return;
            }

            Purchase dollar5Purchase = inventory.getPurchase(SKU_DOLLAR_5);
            if (dollar5Purchase != null) {
                mIabHelper.consumeAsync(inventory.getPurchase(SKU_DOLLAR_5), mConsumeFinishedListener);
                return;
            }

            Purchase dollar10Purchase = inventory.getPurchase(SKU_DOLLAR_10);
            if (dollar10Purchase != null) {
                mIabHelper.consumeAsync(inventory.getPurchase(SKU_DOLLAR_10), mConsumeFinishedListener);
                return;
            }
        }
    };

    // Called when consumption is complete
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
        }
    };


    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
            = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            if (result.isFailure()) {
                return;
            } else {
                // Thank the user
                Toast.makeText(AboutActivity.this, R.string.thank_you_for_donating, Toast.LENGTH_SHORT).show();
                // Consume the purchase
                mIabHelper.consumeAsync(purchase, mConsumeFinishedListener);
                return;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.about_activity_menu, menu);

        menu.findItem(R.id.donate).setVisible(mIsIabSetup);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        String sku = "";

        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.rate_on_google_play:
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                return true;
            case R.id.donate_1:
                sku = SKU_DOLLAR_1;
                break;
            case R.id.donate_2:
                sku = SKU_DOLLAR_2;
                break;
            case R.id.donate_5:
                sku = SKU_DOLLAR_5;
                break;
            case R.id.donate_10:
                sku = SKU_DOLLAR_10;
                break;
        }

        if (!TextUtils.isEmpty(sku)) {
            // IAP
            mIabHelper.launchPurchaseFlow(this, sku, REQUEST_CODE_IAB, mPurchaseFinishedListener, "asdf");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mIabHelper != null) mIabHelper.dispose();
        mIabHelper = null;
    }
}
