package com.example.ui.exportimport

import com.example.domain.model.ConnectionType
import com.example.domain.model.DeviceType
import com.example.domain.model.RemoteDevice
import org.json.JSONObject
import java.util.UUID

object ProfileImportExportHelper {

    fun exportProfileToJson(device: RemoteDevice): String {
        val json = JSONObject()
        json.put("appVersion", "1.0.0")
        json.put("exportTime", System.currentTimeMillis())

        val devObj = JSONObject()
        devObj.put("name", device.name)
        devObj.put("brand", device.brand)
        devObj.put("model", device.model)
        devObj.put("deviceType", device.deviceType.name)
        devObj.put("connectionType", device.connectionType.name)
        devObj.put("protocolId", device.protocolId)
        devObj.put("carrierFrequency", device.carrierFrequency)
        devObj.put("ipAddress", device.ipAddress ?: "")

        json.put("device", devObj)
        return json.toString(2)
    }

    fun importProfileFromJson(jsonStr: String): RemoteDevice? {
        return try {
            val json = JSONObject(jsonStr)
            val devObj = json.getJSONObject("device")

            RemoteDevice(
                id = UUID.randomUUID().toString(),
                name = devObj.optString("name", "Imported Device"),
                brand = devObj.optString("brand", "Generic"),
                model = devObj.optString("model", "Generic"),
                deviceType = try { DeviceType.valueOf(devObj.optString("deviceType", "TV")) } catch (e: Exception) { DeviceType.TV },
                connectionType = try { ConnectionType.valueOf(devObj.optString("connectionType", "IR")) } catch (e: Exception) { ConnectionType.IR },
                protocolId = devObj.optString("protocolId", "nec"),
                carrierFrequency = devObj.optInt("carrierFrequency", 38000),
                ipAddress = devObj.optString("ipAddress").ifEmpty { null }
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
