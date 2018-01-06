package net.frostedbytes.android.trendo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import net.frostedbytes.android.trendo.fragment.MatchListFragment;

public class  MainActivity extends AppCompatActivity {

  private static final String TAG = "MainActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // create the adapter that will return a fragment for each section
    FragmentPagerAdapter pagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

      private final Fragment[] mFragments = new Fragment[]{
          new MatchListFragment(),
      };

      private final String[] mFragmentNames = new String[]{
          "Matches"
      };

      @Override
      public Fragment getItem(int position) {
        return mFragments[position];
      }

      @Override
      public int getCount() {
        return mFragments.length;
      }

      @Override
      public CharSequence getPageTitle(int position) {
        return mFragmentNames[position];
      }
    };

    // Set up the ViewPager with the sections adapter.
    ViewPager viewPager = findViewById(R.id.container);
    viewPager.setAdapter(pagerAdapter);
    TabLayout tabLayout = findViewById(R.id.tabs);
    tabLayout.setupWithViewPager(viewPager);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int i = item.getItemId();
    if (i == R.id.action_logout) {
      FirebaseAuth.getInstance().signOut();
      startActivity(new Intent(this, SignInActivity.class));
      finish();
      return true;
    } else {
      return super.onOptionsItemSelected(item);
    }
  }
}
