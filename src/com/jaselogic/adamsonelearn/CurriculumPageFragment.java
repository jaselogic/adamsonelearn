package com.jaselogic.adamsonelearn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
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
		
		private Curriculum mCurriculum = new Curriculum();
		
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
			Log.d("CREATE", "CREATE");
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
						Log.d("YEAR", String.valueOf(yr));
						Log.d("SEM", String.valueOf(sem));
						
						//skip div with CCC
						itr.next();
						
						Element innest;
						while(itr.hasNext() && !(innest = itr.next()).attr("style").equals("height:20px;")) {
							if(innest.hasClass("curr")) {
								//GET PK, SUBJCODE, SUBJNAME, UNITS, PREREQ, COREQ
								Elements item = innest.select(":root > table > tbody > tr");
								/*
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
										+ "]");*/
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
										Log.d("ELEC", elec.select("span:nth-of-type(1)").text().trim() + " " + 
												elec.select("span:nth-of-type(2)").text().trim() + " " + 
												elec.ownText().trim() );
										
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
				//LOGTEST HERE

				
				//DATABASE
				//Open database, or create if not yet created
				SQLiteDatabase eLearnDb = getActivity().openOrCreateDatabase("AdUELearn", Context.MODE_PRIVATE, null);
			
				//DROP TABLE IF IT EXISTS
				eLearnDb.execSQL("DROP TABLE IF EXISTS SubjTable");
				eLearnDb.execSQL("DROP TABLE IF EXISTS PrereqTable");
				eLearnDb.execSQL("DROP TABLE IF EXISTS CoreqTable");
				eLearnDb.execSQL("DROP TABLE IF EXISTS ElecTable");
							
				//CREATE TABLES
				eLearnDb.execSQL("CREATE TABLE SubjTable " + 
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
				String sqlSubj = "INSERT INTO SubjTable VALUES (?,?,?,?,?,?,?,?,?);";
				String sqlPrereq = "INSERT INTO PrereqTable VALUES (?,?)";
				String sqlCoreq = "INSERT INTO CoreqTable VALUES (?,?)";
				String sqlElec = "INSERT INTO ElecTable VALUES (?,?)";
				
				SQLiteStatement stSubj = eLearnDb.compileStatement(sqlSubj);
				SQLiteStatement stPrereq = eLearnDb.compileStatement(sqlPrereq);
				SQLiteStatement stCoreq = eLearnDb.compileStatement(sqlCoreq);
				SQLiteStatement stElec = eLearnDb.compileStatement(sqlElec);
				Log.d("CURRI", "CURRI");							
				//Iterate insert
				eLearnDb.beginTransaction();
				Iterator<ArrayList<Subject>> subjItr = mCurriculum.iterator();
				int yrCnt = 1;
				while(subjItr.hasNext()) {
					ArrayList<Subject> subjList = subjItr.next();
					Log.d("YEAR!", String.valueOf(yrCnt++));
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
						
						//IF HASELEC, add to ElecTable, then add the subject to the Subjtable
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
								stSubj.execute(); stSubj.execute();
							}
						}
					}
				}
				eLearnDb.setTransactionSuccessful();
				eLearnDb.endTransaction();
				
				
				//eLearnDb.execSQL("INSERT INTO TestTable VALUES " + 
				//			"('VallarNew', 'Justin', 20)");
				
				//rawQuery
				//eLearnDb.
				//Cursor c = eLearnDb.rawQuery("SELECT * FROM TestTable", null);
				//while(c.moveToNext()) {
				//	Log.d("JUS!", c.getString(c.getColumnIndex("LastName")));
				//}
				//Close database.
				eLearnDb.close();
				
				//Log.d("JUS!", curriculum.select(SELECTOR_YEAR).html());
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
}
