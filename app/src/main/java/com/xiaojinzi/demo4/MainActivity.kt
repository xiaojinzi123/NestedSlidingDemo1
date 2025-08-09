package com.xiaojinzi.demo4

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.dp
import com.xiaojinzi.demo4.ui.theme.Demo4Theme
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Demo4Theme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                ) { innerPadding ->
                    Column(
                        modifier = Modifier,
                    ) {
                        var headerViewHeight: Int? by remember {
                            mutableStateOf(
                                value = null,
                            )
                        }
                        // 有正负
                        var headerViewOffsetY by remember {
                            mutableFloatStateOf(
                                value = 0f,
                            )
                        }
                        val scrollState1 = rememberLazyListState()
                        val scrollState2 = rememberLazyListState()
                        val scrollState3 = rememberLazyListState()
                        var targetScrollState by remember {
                            mutableStateOf(
                                value = scrollState1,
                            )
                        }
                        val pageState = rememberPagerState {
                            3
                        }
                        LaunchedEffect(key1 = pageState.targetPage) {
                            when (pageState.targetPage) {
                                0 -> {
                                    targetScrollState = scrollState1
                                }

                                1 -> {
                                    targetScrollState = scrollState2
                                }

                                2 -> {
                                    targetScrollState = scrollState3
                                }
                            }
                        }
                        val nestedScrollConnection = remember {
                            object : NestedScrollConnection {
                                // 滑动前的事件处理（优先拦截事件）
                                override fun onPreScroll(
                                    available: Offset,
                                    source: NestedScrollSource
                                ): Offset {

                                    val headerViewHeight = headerViewHeight

                                    if (headerViewHeight == null) {
                                        return Offset.Zero
                                    }

                                    if (available.x.absoluteValue > available.y.absoluteValue) {
                                        return Offset.Zero
                                    }

                                    // 是否在顶部
                                    val isAtTop = targetScrollState.firstVisibleItemIndex == 0 &&
                                            targetScrollState.firstVisibleItemScrollOffset == 0

                                    Log.e(
                                        "NestedScrollConnection",
                                        "onPreScroll: available=$available, canScrollUp=$isAtTop"
                                    )

                                    // 如果向上滑动, 就是内容往上面跑
                                    if (isAtTop && available.y < 0) {
                                        val diffY =
                                            if ((headerViewOffsetY + available.y) < -headerViewHeight.toFloat()) {
                                                -headerViewHeight.toFloat() - headerViewOffsetY
                                            } else {
                                                available.y
                                            }
                                        headerViewOffsetY += diffY
                                        // 消耗滑动事件，用于调整头部高度
                                        // 返回消耗的偏移量（表示事件已被处理）
                                        return available.copy(
                                            y = diffY,
                                        )
                                    } else if (available.y > 0) {
                                        val diffY =
                                            if ((headerViewOffsetY + available.y) >= 0f) {
                                                -headerViewOffsetY
                                            } else {
                                                available.y
                                            }
                                        headerViewOffsetY += diffY
                                        // 消耗滑动事件，用于调整头部高度
                                        // 返回消耗的偏移量（表示事件已被处理）
                                        return available.copy(
                                            y = diffY,
                                        )
                                    }
                                    // 不消耗事件，交给LazyList处理
                                    return Offset.Zero
                                }
                            }
                        }

                        // 这块是 Header 区域, 你自己随便写视图
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .layout { measurable, constraints ->
                                    // 测量子组件
                                    val placeable = measurable.measure(
                                        constraints.copy(
                                            maxHeight = Int.MAX_VALUE,
                                        )
                                    )

                                    headerViewHeight = placeable.height

                                    // 设置偏移量
                                    layout(
                                        width = placeable.width,
                                        height = (placeable.height + headerViewOffsetY.roundToInt()).coerceAtLeast(
                                            minimumValue = 0,
                                        ),
                                    ) {
                                        placeable.placeRelative(0, headerViewOffsetY.roundToInt())
                                    }
                                }
                                .background(
                                    color = Color.Red,
                                )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(ratio = 2f)
                                    // .height(100.dp)
                                    .background(Color.Blue),
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(ratio = 2f)
                                    .background(Color.DarkGray),
                            )
                        }

                        // 这里是 HorizontalPager 下面的几个列表
                        HorizontalPager(
                            modifier = Modifier
                                .nestedScroll(nestedScrollConnection),
                            state = pageState,
                        ) //
                        { pageIndex ->
                            when (pageIndex) {
                                0 -> {
                                    LazyColumn(
                                        state = scrollState1,
                                    ) {
                                        items(
                                            items = (0..100).toList(),
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(8.dp)
                                                    .background(Color.Green)
                                                    .padding(16.dp)
                                            ) {
                                                Text(text = "Item $it")
                                            }
                                        }
                                    }
                                }

                                1 -> {
                                    LazyColumn(
                                        modifier = Modifier,
                                        state = scrollState2,
                                    ) {
                                        items(
                                            items = (0..100).toList(),
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(8.dp)
                                                    .background(Color.Yellow)
                                                    .padding(16.dp)
                                            ) {
                                                Text(text = "Item $it")
                                            }
                                        }
                                    }
                                }

                                2 -> {
                                    LazyColumn(
                                        state = scrollState3,
                                    ) {
                                        items(
                                            items = (0..100).toList(),
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(8.dp)
                                                    .background(Color.Cyan)
                                                    .padding(16.dp)
                                            ) {
                                                Text(text = "Item $it")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}