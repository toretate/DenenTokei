package com.toretate.denentokei;

import android.content.ClipData;
import android.graphics.Canvas;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.widget.RelativeLayout;

/**
 * ドラッグ&ドロップ用のリスナ
 */
public class DandDListener implements GestureDetector.OnGestureListener, View.OnDragListener {
	
	private @NonNull View m_target;				//!< D&D対象のView
	private @NonNull Point m_touchPointInTarget;	//!< m_target内部のタッチ位置
	
	public DandDListener( final @NonNull View target ) {
		m_target = target;
		m_touchPointInTarget = new Point( m_target.getWidth() /2, m_target.getHeight() /2 );	// 初期値では中央としておく
	}

	@Override
	public boolean onDrag( final View v, final DragEvent event) {
		switch( event.getAction() ) {
		case DragEvent.ACTION_DRAG_STARTED:
		case DragEvent.ACTION_DRAG_ENTERED:
		case DragEvent.ACTION_DRAG_LOCATION:
		case DragEvent.ACTION_DRAG_EXITED:
			return true;
			
		case DragEvent.ACTION_DROP:
		{
			final View view = (View)event.getLocalState();
			final RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams)view.getLayoutParams();
			
			int l = (int)event.getX() - m_touchPointInTarget.x;
			int t = (int)event.getY() - m_touchPointInTarget.y;
			int r = param.rightMargin;
			int b = param.bottomMargin;
			param.setMargins( l, t, r, b );
			view.setLayoutParams( param );
			
//			if( view.getId() == R.id.chaClock ) {
//				int l = (int)event.getX() - m_touchPointInTarget.x;
//				int t = (int)event.getY() - m_touchPointInTarget.y;
//				int r = param.rightMargin;
//				int b = param.bottomMargin;
//				param.setMargins( l, t, r, b );
//				view.setLayoutParams( param );
//			} else {
//				View parent = (View)view.getParent();
//				int l = (int)event.getX() - m_touchPointInTarget.x;
//				int t = param.topMargin;
//				int r = param.rightMargin;
//				int b = parent.getHeight() - ( (int)event.getY() - m_touchPointInTarget.y ) - view.getHeight();
//				param.setMargins( l, t, r, b );
//				view.setLayoutParams( param );
//			}
			
			return true;
		}
			
		default:
			return false;
		}
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onLongPress( final MotionEvent e ) {
        // ドラッグ&ドロップで受け渡しするデータ(使わないのでダミー)  
        final ClipData tmpData = null;
        m_touchPointInTarget = new Point( (int)e.getX(), (int)e.getY() );
        
        // ドラッグ中に表示するイメージのビルダー
		final DragShadowBuilder shadow = new DragShadowBuilder( m_target ) {

			// 標準ではShadowの中央をドラッグするので、元々のドラッグ位置をドラッグするように変更
			@Override
			public void onProvideShadowMetrics( Point shadowSize, Point shadowTouchPoint )
			{
				final View view = this.getView();
				if ( view == null ) {
					super.onProvideShadowMetrics( shadowSize, shadowTouchPoint );
					return;
				}
				
				shadowSize.set( view.getWidth(), view.getHeight() );
				// タッチ位置の補正
				final int x = m_touchPointInTarget.x;
				final int y = m_touchPointInTarget.y;
				shadowTouchPoint.set( x, y );
			}

			@Override
			public void onDrawShadow( Canvas canvas ) {
				super.onDrawShadow( canvas );
			}
		};
  
        // ドラッグを開始  
        m_target.startDrag(tmpData, shadow, m_target, 0);
		((View)m_target.getParent()).setOnDragListener( this );
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return false;
	}
}
