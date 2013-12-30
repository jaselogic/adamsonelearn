package com.jaselogic.adamsonelearn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jaselogic.adamsonelearn.DocumentManager.DocumentCookie;
import com.jaselogic.adamsonelearn.DocumentManager.ResponseReceiver;
import com.jaselogic.adamsonelearn.YearSelectAdapter.YearSelectListItem;

class CurriculumPageFragment {
	public static class YearSelectFragment extends ListFragment
			implements ResponseReceiver {
		public final static String SELECTOR_CURRICULUM_PAGE = "div.contentcontainer2 > table > tbody > tr > td:nth-of-type(2) > div:nth-of-type(3) div";
		public final static String SELECTOR_YEAR = "div[style*=background:#FF9]";
		
		private String cookie;
		private YearSelectAdapter adapter;
		private ArrayList<YearSelectListItem> yearArrayList;
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View pageRootView = inflater.inflate(R.layout.fragment_updates, 
					container, false);
				
			yearArrayList = new ArrayList<YearSelectListItem>();	
			adapter = new YearSelectAdapter(getActivity(), yearArrayList);
			setListAdapter(adapter);
			
			//get original cookie
			cookie = ((Dashboard)getActivity()).cookie;
			
			//TODO: remove strings stdno pw
			new DocumentManager.DownloadDocumentTask(YearSelectFragment.this, 
				DocumentManager.PAGE_CURRICULUM, cookie).execute("stdno", "pw");
			
			return pageRootView;
		}
		
		@Override
		public void onResourceReceived(DocumentCookie result)
				throws IOException {
			if(result != null) {
				Elements curriculum = result.document.select(SELECTOR_CURRICULUM_PAGE);
				Iterator<Element> itr = curriculum.iterator();
				
				Pattern pattern = Pattern.compile("FF9");
				String currentYear = "First Year";
				int yr = 1;
				int sem = 0;
				Matcher matcher;
				
				while(itr.hasNext()) {
					Element inner = itr.next();
					matcher = pattern.matcher(inner.attr("style"));
					if(matcher.find()) {
						//GET YEAR, SEMESTER
						sem++;
						String newYear = inner.text().trim().substring(0, inner.text().indexOf(','));
						if (!currentYear.equals(newYear)) {
							currentYear = newYear;
							yr++;
							sem = 1;
						}
						Log.d("YEAR", String.valueOf(yr));
						Log.d("SEM", String.valueOf(sem));
						
						//skip div with CCC
						itr.next();
						
						Element innest;
						while(itr.hasNext() && !(innest = itr.next()).attr("style").equals("height:20px;")) {
							if(innest.hasClass("curr")) {
								//GET PK, SUBJCODE, SUBJNAME, UNITS, PREREQ, COREQ
								Elements item = innest.select(":root > table > tbody > tr");
								Log.d("SUBJ",
										item.first().select("td:nth-of-type(1)").text().trim() + " " +
										item.first().select("td:nth-of-type(2)").text().trim() + " " +
										item.first().select("td:nth-of-type(3)").text().trim() + " " +
										item.first().select("td:nth-of-type(4)").text().trim() + " " +
										"<" +
										item.first().select("td:nth-of-type(5) span").text().trim()
										+">" + " " +
										"[" +
										item.first().select("td:nth-of-type(6) span").text().trim()
										+ "]");
								
								//CHECK IF THERE ARE ELECTIVES
								if(item.size() > 1) {
									Elements elecs = item.select("tbody div");
									Iterator<Element> elecItr = elecs.iterator();
									//GET PK, SUBJCODE, SUBJNAME
									while(elecItr.hasNext()) {
										Element elec = elecItr.next();
										Log.d("ELEC", elec.select("span:nth-of-type(1)").text().trim() + " " + 
												elec.select("span:nth-of-type(2)").text().trim() + " " + 
												elec.ownText().trim() );
									}
								}
							}
						}
						//Log.d("SUBJ", msg)
					}
				}
				
				//Log.d("JUS!", curriculum.select(SELECTOR_YEAR).html());
			} else {
				Log.d("JUS!", "WALANG DOCUMENT");
			}
		}
		
		//END ONRESOURCERECEIVED
	}
}
