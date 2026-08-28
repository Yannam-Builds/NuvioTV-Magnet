package com.nuvio.tv.domain.deeplink

sealed interface AppDeepLink {
    data class Meta(
        val type: String,
        val id: String
    ) : AppDeepLink

    data class AddonInstall(
        val manifestUrl: String
    ) : AppDeepLink

    data class Magnet(
        val infoHash: String,
        val displayName: String?,
        val trackers: List<String>
    ) : AppDeepLink
}
