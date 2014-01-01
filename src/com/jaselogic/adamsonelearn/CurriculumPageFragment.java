package com.jaselogic.adamsonelearn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.jaselogic.adamsonelearn.CurrDisplayAdapter.CurrDisplayListItem;
import com.jaselogic.adamsonelearn.CurrDisplayAdapter.ItemType;
import com.jaselogic.adamsonelearn.DocumentManager.DocumentCookie;
import com.jaselogic.adamsonelearn.DocumentManager.ResponseReceiver;
import com.jaselogic.adamsonelearn.YearSelectAdapter.YearSelectListItem;

class CurriculumPageFragment {
	public static class YearSelectFragment extends ListFragment
			implements ResponseReceiver {
		public final static String SELECTOR_CURRICULUM_PAGE = "div.contentcontainer2 > table > tbody > tr > td:nth-of-type(2) > div:nth-of-type(3) div";
		public final static String SELECTOR_YEAR = "div[style*=background:#FF9]";
		public final static String[] YEAR_NAMES = {
			"First Year",
			"Second Year",
			"Third Year",
			"Fourth Year",
			"Fifth Year"
		};
		
		private NonSwipeViewPager parentViewPager;
		
		private String cookie;
		private YearSelectAdapter adapter;
		private ArrayList<YearSelectListItem> yearArrayList;
		
		private Curriculum mCurriculum = new Curriculum();
				
		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			// TODO Auto-generated method stub
			Intent intent = new Intent("page-change-event");
			intent.putExtra("page", String.valueOf(position + 1));
			LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
			parentViewPager.setCurrentItem(1);
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View pageRootView = inflater.inflate(R.layout.fragment_listview, 
					container, false);
				
			yearArrayList = new ArrayList<YearSelectListItem>();	
			adapter = new YearSelectAdapter(getActivity(), yearArrayList);
			setListAdapter(adapter);
			
			//get original cookie
			cookie = ((Dashboard)getActivity()).cookie;
			Log.d("CREATE", "CREATE");
			//TODO: remove strings stdno pw
			new DocumentManager.DownloadDocumentTask(YearSelectFragment.this, 
				DocumentManager.PAGE_CURRICULUM, cookie).execute("stdno", "pw");
			
			//get parent viewpager
			parentViewPager = (NonSwipeViewPager) getActivity().findViewById(R.id.curriculum_pager);
			
			return pageRootView;
		}
		
		@Override
		public void onResourceReceived(DocumentCookie result)
				throws IOException {
			if(result != null) {
				Elements curriculum = result.document.select(SELECTOR_CURRICULUM_PAGE);
				Iterator<Element> itr = curriculum.iterator();
				
				//year has a background style of #FF9 in webpage
				Pattern pattern = Pattern.compile("FF9");
				String currentYear = "First Year";
				int yr = 1;
				int sem = 0;
				Matcher matcher;
				
				mCurriculum.add(new ArrayList<Subject>());
				
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
							mCurriculum.add(new ArrayList<Subject>());
						}
						
						//skip div with CCC
						itr.next();
						
						Element innest;
						while(itr.hasNext() && !(innest = itr.next()).attr("style").equals("height:20px;")) {
							if(innest.hasClass("curr")) {
								//GET PK, SUBJCODE, SUBJNAME, UNITS, PREREQ, COREQ
								Elements item = innest.select(":root > table > tbody > tr");

								//create a new subject object
								Subject subj = new Subject();
								subj.year = yr;
								subj.semester = sem;
								subj.pkey = Integer.parseInt(item.first().select("td:nth-of-type(1)").text().trim());
								subj.code = item.first().select("td:nth-of-type(2)").text().trim();
								subj.name = item.first().select("td:nth-of-type(3)").text().trim();
								subj.units = Integer.parseInt(item.first().select("td:nth-of-type(4)").text().trim());
								
								//CHECK FOR PREREQUISITES
								Elements prereqElem = item.first().select("td:nth-of-type(5) span");
								subj.hasPrereq = !prereqElem.isEmpty();
								if(subj.hasPrereq) {
									Iterator<Element> prereqItr = prereqElem.iterator();
									while(prereqItr.hasNext()) {
										Element prereqItem = prereqItr.next();
										subj.prereqCodeList.add(Integer.parseInt(prereqItem.text().trim()));
									}
								}
								
								//CHECK FOR COREQUISITES
								Elements coreqElem = item.first().select("td:nth-of-type(6) span");
								subj.hasCoreq = !coreqElem.isEmpty();
								if(subj.hasCoreq) {
									Iterator<Element> coreqItr = coreqElem.iterator();
									while(coreqItr.hasNext()) {
										Element coreqItem = coreqItr.next();
										subj.coreqCodeList.add(Integer.parseInt(coreqItem.text().trim()));
									}
								}
															
								//CHECK IF THERE ARE ELECTIVES
								if(item.size() > 1) {
									Elements elecs = item.select("tbody div");
									Iterator<Element> elecItr = elecs.iterator();
									//GET PK, SUBJCODE, SUBJNAME
									subj.hasElec = elecItr.hasNext();
									while(elecItr.hasNext()) {
										Element elec = elecItr.next();

										Subject elecSubj = new Subject();
										elecSubj.pkey = Integer.parseInt(elec.select("span:nth-of-type(1)").text().trim());
										elecSubj.code = elec.select("span:nth-of-type(2)").text().trim();
										elecSubj.name = elec.ownText().trim();
										subj.electiveList.add(elecSubj);
									}
								}
								//add the subject to the current arraylist<subject>
								mCurriculum.getYear(yr).add(subj);
							}
						}
						//Log.d("SUBJ", msg)
					}
				}
				
				//DATABASE
				//Open database, or create if not yet created
				SQLiteDatabase eLearnDb = getActivity().openOrCreateDatabase("AdUELearn", Context.MODE_PRIVATE, null);
			
				//DROP TABLE IF IT EXISTS
				eLearnDb.execSQL("DROP TABLE IF EXISTS CurrTable");
				eLearnDb.execSQL("DROP TABLE IF EXISTS PrereqTable");
				eLearnDb.execSQL("DROP TABLE IF EXISTS CoreqTable");
				eLearnDb.execSQL("DROP TABLE IF EXISTS ElecTable");
							
				//CREATE TABLES
				eLearnDb.execSQL("CREATE TABLE CurrTable " + 
							"(Id INTEGER, SubjCode TEXT, SubjName TEXT, " + 
							"Units INTEGER, Year INTEGER, Semester INTEGER, " +
							"HasPrereq INTEGER, HasCoreq INTEGER, HasElec INTEGER);");
				
				
				eLearnDb.execSQL("CREATE TABLE PrereqTable " + 
							"(SubjId INTEGER, PrereqId INTEGER);");
				
				eLearnDb.execSQL("CREATE TABLE CoreqTable " + 
							"(SubjId INTEGER, CoreqId INTEGER);");
				
				eLearnDb.execSQL("CREATE TABLE ElecTable " + 
							"(SubjId INTEGER, ElecId INTEGER);");
				
				//INSERT MULTIPLE ITEMS.
				String sqlSubj = "INSERT INTO CurrTable VALUES (?,?,?,?,?,?,?,?,?);";
				String sqlPrereq = "INSERT INTO PrereqTable VALUES (?,?)";
				String sqlCoreq = "INSERT INTO CoreqTable VALUES (?,?)";
				String sqlElec = "INSERT INTO ElecTable VALUES (?,?)";
				
				SQLiteStatement stSubj = eLearnDb.compileStatement(sqlSubj);
				SQLiteStatement stPrereq = eLearnDb.compileStatement(sqlPrereq);
				SQLiteStatement stCoreq = eLearnDb.compileStatement(sqlCoreq);
				SQLiteStatement stElec = eLearnDb.compileStatement(sqlElec);

				//Iterate insert
				eLearnDb.beginTransaction();
				Iterator<ArrayList<Subject>> subjItr = mCurriculum.iterator();
				while(subjItr.hasNext()) {
					ArrayList<Subject> subjList = subjItr.next();
					//Log.d("YEAR!", String.valueOf(yrCnt++));
					Iterator<Subject> subjItrTest = subjList.iterator();
					
					while(subjItrTest.hasNext()) {
						Subject testSubj = subjItrTest.next();
						stSubj.clearBindings();
						stSubj.bindLong(1, testSubj.pkey);
						stSubj.bindString(2, testSubj.code);
						stSubj.bindString(3, testSubj.name);
						stSubj.bindLong(4, testSubj.units);
						stSubj.bindLong(5, testSubj.year);
						stSubj.bindLong(6, testSubj.semester);
						stSubj.bindLong(7, testSubj.hasPrereq ? 1 : 0);
						stSubj.bindLong(8, testSubj.hasCoreq ? 1 : 0);
						stSubj.bindLong(9, testSubj.hasElec ? 1 : 0);
						stSubj.execute();
						
						//IF HASPREREQ TRUE, ADD TO PREREQ TABLE
						if(testSubj.hasPrereq) {
							Iterator<Integer> prereqItr = testSubj.prereqCodeList.iterator();
							while(prereqItr.hasNext()) {
								stPrereq.clearBindings();
								stPrereq.bindLong(1, testSubj.pkey);
								stPrereq.bindLong(2, prereqItr.next().intValue());
								stPrereq.execute();
							}
						}
						
						//IF HASCOREQ TRUE, ADD TO COREQ TABLE
						if(testSubj.hasCoreq) {
							Iterator<Integer> coreqItr = testSubj.coreqCodeList.iterator();
							while(coreqItr.hasNext()) {
								stCoreq.clearBindings();
								stCoreq.bindLong(1, testSubj.pkey);
								stCoreq.bindLong(2, coreqItr.next().intValue());
								stCoreq.execute();
							}
						}
						
						//IF HASELEC, add to ElecTable, then add the subject to the Currtable
						if(testSubj.hasElec) {
							Iterator<Subject> elecItr = testSubj.electiveList.iterator();
							while(elecItr.hasNext()) {
								Subject elecSubj = elecItr.next();
								stElec.clearBindings();
								stElec.bindLong(1, testSubj.pkey);
								stElec.bindLong(2, elecSubj.pkey);
								stElec.execute();	
																
								stSubj.clearBindings();
								stSubj.bindLong(1, elecSubj.pkey);
								stSubj.bindString(2, elecSubj.code);
								stSubj.bindString(3, elecSubj.name);
								stSubj.execute();
							}
						}
					}
				}
				eLearnDb.setTransactionSuccessful();
				eLearnDb.endTransaction();
							
				//Close database.
				eLearnDb.close();
				
				//initialize year select
				for(int i = 0; i < yr; i++) {
					YearSelectListItem yearItem = new YearSelectListItem();
					yearItem.year = YEAR_NAMES[i];
					yearItem.imageResId = R.drawable.ic_next;
					yearArrayList.add(yearItem);
				}
				
				adapter.notifyDataSetChanged();

			} else {
				Log.d("JUS!", "WALANG DOCUMENT");
			}
		}
		
		//END ONRESOURCERECEIVED
		
		//TODO: TRANSFER AND REWRITE WITH GET SET
		//TODO: SEPARATE DOWNLOAD CLASS FROM SQLITE OBJECT 
		public static class Subject {
			public int year;
			public int semester;
			public int pkey;
			public int units;
			public String code;
			public String name;
			public boolean hasPrereq;
			public boolean hasCoreq;
			public boolean hasElec;
			public ArrayList<Integer> prereqCodeList;
			public ArrayList<Integer> coreqCodeList;
			
			public ArrayList<Subject> prereqList;
			public ArrayList<Subject> coreqList;
			public ArrayList<Subject> electiveList;
			
			private Subject() {
				hasPrereq = false;
				hasCoreq = false;
				hasElec = false;
				
				prereqCodeList = new ArrayList<Integer>();
				coreqCodeList = new ArrayList<Integer>();
				
				prereqList = new ArrayList<Subject>();
				coreqList = new ArrayList<Subject>();
				electiveList = new ArrayList<Subject>();
			}
		}
		
		public static class Curriculum extends ArrayList<ArrayList<Subject>> {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			public ArrayList<Subject> getYear(int year) {
				return this.get(year - 1);
			}
		}
	}
	
	public static class CurrDisplayFragment extends ListFragment {
		
		private ArrayList<CurrDisplayListItem> currArrayList;
		private CurrDisplayAdapter adapter;
		private NonSwipeViewPager parentViewPager;
		
		public final static String[] SEMESTER_NAMES = {
			"No such semester",
			"First Semester",
			"Second Semester",
			"Summer"
		};
		
		@Override
		public void onResume() {
			// TODO Auto-generated method stub
			super.onResume();
			
			LocalBroadcastManager.getInstance(getActivity())
				.registerReceiver(mMessageReceiver, new IntentFilter("page-change-event"));
			
		}
		
		private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				//TODO: EXTRACT DATA FROM DB ACCORDING TO PAGE.
				String page = intent.getStringExtra("page");
				
				//TODO: DO THIS ON ASYNCTASK
				//clear current list
				currArrayList.clear();
				adapter.notifyDataSetChanged();
				
				//Assert database and tables have been created.
				SQLiteDatabase eLearnDb = getActivity().openOrCreateDatabase("AdUELearn", Context.MODE_PRIVATE, null);
								
				//perform query
				for(int semester = 1; semester <=3; semester++ ) {
					Cursor c = eLearnDb.rawQuery(
							"SELECT * FROM CurrTable WHERE Year = ? AND Semester = ?",
							new String[] {page, String.valueOf(semester)});
					
					CurrDisplayListItem tempItem;
					
					//add titler
					if(c.getCount() > 0) {
						tempItem = new CurrDisplayListItem();
						tempItem.mainText = SEMESTER_NAMES[semester].toUpperCase();
						tempItem.viewType = ItemType.ITEM_TITLE;
						currArrayList.add(tempItem);
					}
					
					while(c.moveToNext()) {
						tempItem = new CurrDisplayListItem();
						tempItem.mainText = c.getString(c.getColumnIndex("SubjName"));
						tempItem.unitsText = "Units: " + c.getString(c.getColumnIndex("Units"));
						
						String subjId = String.valueOf(c.getInt(c.getColumnIndex("Id")));
						//Check for prerequisites.
						if(c.getInt(c.getColumnIndex("HasPrereq")) == 1) {
							Cursor curPrereq = eLearnDb.rawQuery(
								"SELECT SubjCode, SubjName FROM PrereqTable " + 
							    "LEFT JOIN CurrTable ON Id=PrereqId WHERE " + 
								"SubjId = ?", new String[] {subjId});
							
							StringBuilder sb = new StringBuilder();
							while(curPrereq.moveToNext()) {
								sb.append(curPrereq.getString(curPrereq.getColumnIndex("SubjName")));
								if(!curPrereq.isLast()) {
									sb.append("\n");
								}
							}
							tempItem.prereqText = sb.toString();
						} else {
							tempItem.prereqText = "None";
						}
						
						//Check for corequisites.
						if(c.getInt(c.getColumnIndex("HasCoreq")) == 1) {
							Cursor curCoreq = eLearnDb.rawQuery(
								"SELECT SubjCode, SubjName FROM CoreqTable " + 
							    "LEFT JOIN CurrTable ON Id=CoreqId WHERE " + 
								"SubjId = ?", new String[] {subjId});
							
							StringBuilder sb = new StringBuilder();
							while(curCoreq.moveToNext()) {
								sb.append(curCoreq.getString(curCoreq.getColumnIndex("SubjName")));
								if(!curCoreq.isLast()) {
									sb.append("\n");
								}
							}
							tempItem.coreqText = sb.toString();
						} else {
							tempItem.coreqText = "None";
						}
						
						//Check for electives.
						if(c.getInt(c.getColumnIndex("HasElec")) == 1) {
							tempItem.viewType = ItemType.ITEM_ELECTIVE;
							Cursor curElec = eLearnDb.rawQuery(
								"SELECT SubjCode, SubjName FROM ElecTable " + 
							    "LEFT JOIN CurrTable ON Id=ElecId WHERE " + 
								"SubjId = ?", new String[] {subjId});
							
							StringBuilder sb = new StringBuilder();
							while(curElec.moveToNext()) {
								sb.append(curElec.getString(curElec.getColumnIndex("SubjName")));
								if(!curElec.isLast()) {
									sb.append("\n");
								}
							}
							tempItem.elecText = sb.toString();
						} else {
							tempItem.elecText = "None";
							tempItem.viewType = ItemType.ITEM_REGULAR;
						}
						currArrayList.add(tempItem);
					}
				}
				
				eLearnDb.close();

				adapter.notifyDataSetChanged();
			}
		};
		
		@Override
		public void onPause() {
			super.onPause();
			LocalBroadcastManager.getInstance(getActivity())
				.unregisterReceiver(mMessageReceiver);
		};
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View pageRootView = inflater.inflate(R.layout.fragment_listview, 
					container, false);
				
			currArrayList = new ArrayList<CurrDisplayListItem>();	
			adapter = new CurrDisplayAdapter(getActivity(), currArrayList);
			setListAdapter(adapter);

			Log.d("CREATE", "CREATE");
			//get results with async task
			
			//get parent viewpager
			parentViewPager = (NonSwipeViewPager) getActivity().findViewById(R.id.curriculum_pager);
						
			return pageRootView;
		}
	}
}
