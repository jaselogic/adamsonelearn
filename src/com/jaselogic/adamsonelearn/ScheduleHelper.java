package com.jaselogic.adamsonelearn;

import java.util.regex.Pattern;

import android.text.format.Time;
import android.util.Log;

public class ScheduleHelper {
	public static int convertTimeToIntSlot(Time t) {
		int slot = 0;
		
		slot = (t.hour - 7) * 2;
		if(t.minute >= 30)
			slot++;
		
		return slot;
	}
	
	public static int convertStringToIntSlot(String timeSlotString) {
		int hour = Integer.parseInt(timeSlotString.substring(0, 2));
		int slot = -1;
		
		slot = (hour - 7) * 2;
		if (Integer.parseInt(timeSlotString.substring(3, 5)) == 30)
			slot++;
		
		return slot;
	}
	
	public static String convertIntToStringSlot(int start, int end) {
		StringBuilder sb = new StringBuilder(
				String.format( "%02d",((start / 2) + 7) )
				);
		
		sb.append(":");
		sb.append( (start % 2) == 0 ? "00" : "30" );
		sb.append("-");
		sb.append(
				String.format( "%02d",((end / 2) + 7) )
				);
		sb.append(":");
		sb.append( (end % 2) == 0 ? "00" : "30" );
		
		return sb.toString();
	}
	
	public static int convertStringToIntDaySlot(String daySlotString) {
		int daySlot = 0;
		final String[] daySlotPatterns = new String[] {
				"M", "T[WTu]", "W", "Th", "F", "S"
			};
			
		for(int j = 0; j < 6; j++) {
			Pattern pattern = Pattern.compile(daySlotPatterns[j]);
			if(pattern.matcher(daySlotString).find()) {
				daySlot |= (1 << j);
			}
		}
		return daySlot;
	}
	
	public static String convertIntToStringDaySlot(int slot) {
		String res;
		final String[] daySlotFull = new String[] {
			"Mon", "Tue", "Wed", "Thu", "Fri", "Sat"
		};
		final String[] daySlotShort = new String[] {
			"M", "T", "W", "Th", "F", "S"
		};
		
		Log.d("FULL", String.valueOf( (slot & (slot - 1)) == 0 ));
		
		//determine if power of two to decide if full or short
		if( (slot & (slot - 1)) == 0 ) { // full
			int c = 0;
			while( (slot >>= 1) != 0) {
				c++;
			}
			res = daySlotFull[c];
		} else { //short
			StringBuilder sb = new StringBuilder();
			for(int c = 0; slot != 0; c++) {
				if ( (slot & 1) == 1 ) {
					sb.append(daySlotShort[c]);
				}
				slot >>= 1;
			}
			res = sb.toString();
		}

		return res;
	}
}
