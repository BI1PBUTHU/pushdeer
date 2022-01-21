package com.pushdeer.os.ui.compose.page.main

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pushdeer.os.R
import com.pushdeer.os.data.api.data.request.DeviceInfo
import com.pushdeer.os.holder.RequestHolder
import com.pushdeer.os.ui.compose.componment.CardItemSingleLineWithIcon
import com.pushdeer.os.ui.compose.componment.ListBottomBlankItem
import com.pushdeer.os.ui.compose.componment.SwipeToDismissItem
import com.pushdeer.os.ui.navigation.Page
import com.pushdeer.os.util.SystemUtil


@ExperimentalMaterialApi
@Composable
fun DeviceListPage(requestHolder: RequestHolder) {
    MainPageFrame(
        titleStringId = Page.Devices.labelStringId,
        onSideIconClick = {
            if (requestHolder.settingStore.thisDeviceId == "") {
                requestHolder.alert.alert(
                    title = R.string.global_alert_title_confirm,
                    content = R.string.alert_device_register_failed_push_sdk,
                    onOk = {})
                // device regid got failed
            } else {
                requestHolder.deviceReg(
                    deviceInfo = DeviceInfo().apply {
                        name = SystemUtil.getDeviceModel()
                        device_id = requestHolder.settingStore.thisDeviceId
                        is_clip = 0
                    }
                )
            }
        },
        showSideIcon = requestHolder.pushDeerViewModel.shouldRegDevice()
    ) {
        if (requestHolder.pushDeerViewModel.deviceList.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(id = R.string.main_device_list_placeholder)
                )
            }
        } else {
            val state = rememberLazyListState()
            LazyColumn(state = state) {
                items(
                    items = requestHolder.pushDeerViewModel.deviceList,
                    key = { item: DeviceInfo -> item.id }) { deviceInfo: DeviceInfo ->
                    var name by remember {
                        mutableStateOf(deviceInfo.name)
                    }
                    SwipeToDismissItem(
                        onAction = { requestHolder.deviceRemove(deviceInfo) }
                    ) {
                        CardItemSingleLineWithIcon(
                            onClick = {
                                requestHolder.alert.alert(
                                    title = R.string.main_device_alert_changedevicename,
                                    content = {
                                        Column {
//                                            Text(text = "type:${deviceInfo.type}")
                                            TextField(
                                                value = name,
                                                onValueChange = { name = it },
                                                shape = RoundedCornerShape(6.dp),
                                                singleLine = true,
                                                maxLines = 1,
                                                label = { Text(text = stringResource(id = R.string.main_device_alert_devicename)) },
                                                colors = TextFieldDefaults.textFieldColors(
                                                    focusedIndicatorColor = Color.Transparent,
                                                    unfocusedIndicatorColor = Color.Transparent,
                                                    disabledIndicatorColor = Color.Transparent,
                                                    errorIndicatorColor = Color.Transparent,
                                                )
                                            )
                                        }
                                    },
                                    onOk = {
                                        deviceInfo.name = name
                                        requestHolder.deviceRename(deviceInfo)
                                    }
                                )
                            },
                            resId = R.drawable.ipad_landscape2x,
                            text = if (deviceInfo.device_id == requestHolder.settingStore.thisDeviceId) "${deviceInfo.name} (${
                                stringResource(
                                    id = R.string.main_device_this_device
                                )
                            }) " else deviceInfo.name
                        )
                        Log.d("WH_", "DeviceListPage: $deviceInfo")
                    }
                }
                item {
                    ListBottomBlankItem()
                }
            }
        }
    }
}