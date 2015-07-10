package com.toretate.denentokei2.preset;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import butterknife.ButterKnife;
import butterknife.Bind;

import com.toretate.denentokei2.R;

public class PresetChaStaAdapterHolder {
	/** 1行内のアイテム数 */
	private static final int s_itemIds[] = { R.id.cha_sta_item1, R.id.cha_sta_item2/*, R.id.cha_sta_item3*/ };
	public static final int s_cols = s_itemIds.length;
	
	@Bind(R.id.cha_sta_item1) Button item1;
	@Bind(R.id.cha_sta_item2) Button item2;
//	@Bind(R.id.cha_sta_item3) Button item3;
	
	public PresetChaStaAdapterHolder( final @NonNull View view ) {
		ButterKnife.bind(this, view);
	}

	@Nullable public Button get( final int index ) {
		switch( index ) {
		case 0: return item1;
		case 1: return item2;
//		case 2: return item3;
		default: return item1;
		}
	}
}
