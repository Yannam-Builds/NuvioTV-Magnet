package com.nuvio.tv.core.deeplink

import com.nuvio.tv.domain.deeplink.AppDeepLink
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DeepLinkParserTest {
    @Test
    fun parsesMetaQueryDeepLink() {
        assertEquals(
            AppDeepLink.Meta(type = "series", id = "tt0944947"),
            DeepLinkParser.parse("nuvio://meta?type=series&id=tt0944947")
        )
    }

    @Test
    fun parsesAddonInstallDeepLink() {
        assertEquals(
            AppDeepLink.AddonInstall("https://free.nebulapro.xyz/sports/i/free/manifest.json"),
            DeepLinkParser.parse("nuvio://free.nebulapro.xyz/sports/i/free/manifest.json")
        )
    }

    @Test
    fun parsesStremioAddonInstallDeepLink() {
        assertEquals(
            AppDeepLink.AddonInstall("https://free.nebulapro.xyz/sports/i/free/manifest.json"),
            DeepLinkParser.parse("stremio://free.nebulapro.xyz/sports/i/free/manifest.json")
        )
    }

    @Test
    fun parsesDirectImdbDetailDeepLink() {
        assertEquals(
            AppDeepLink.Meta(type = "series", id = "tt0944947"),
            DeepLinkParser.parse("nuvio://series/tt0944947")
        )
    }

    @Test
    fun parsesProviderImdbDetailDeepLink() {
        assertEquals(
            AppDeepLink.Meta(type = "series", id = "tt0944947"),
            DeepLinkParser.parse("nuvio://imdb/series/tt0944947")
        )
    }

    @Test
    fun parsesProviderTmdbDetailDeepLink() {
        assertEquals(
            AppDeepLink.Meta(type = "series", id = "tmdb:1399"),
            DeepLinkParser.parse("nuvio://tmdb/tv/1399")
        )
    }

    @Test
    fun doesNotTreatAuthLinkAsAddonInstall() {
        assertNull(DeepLinkParser.parse("nuvio://auth/trakt?code=abc"))
    }

    @Test
    fun doesNotTreatNonHostStremioLinkAsAddonInstall() {
        assertNull(DeepLinkParser.parse("stremio://detail/series/tt0944947"))
    }

    @Test
    fun parsesMagnetDeepLink() {
        val link = "magnet:?xt=urn:btih:0123456789ABCDEF0123456789ABCDEF01234567" +
            "&dn=Test%20Movie" +
            "&tr=udp%3A%2F%2Ftracker.example%3A80%2Fannounce" +
            "&tr=https%3A%2F%2Ftracker.example%2Fannounce"

        assertEquals(
            AppDeepLink.Magnet(
                infoHash = "0123456789abcdef0123456789abcdef01234567",
                displayName = "Test Movie",
                trackers = listOf(
                    "udp://tracker.example:80/announce",
                    "https://tracker.example/announce"
                )
            ),
            DeepLinkParser.parse(link)
        )
    }

    @Test
    fun parsesBase32MagnetHash() {
        assertEquals(
            AppDeepLink.Magnet(
                infoHash = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567",
                displayName = null,
                trackers = emptyList()
            ),
            DeepLinkParser.parse("magnet:?xt=urn:btih:abcdefghijklmnopqrstuvwxyz234567")
        )
    }

    @Test
    fun rejectsMagnetWithInvalidHash() {
        assertNull(DeepLinkParser.parse("magnet:?xt=urn:btih:not-a-valid-hash"))
    }

    @Test
    fun filtersUnsafeMagnetTrackers() {
        val link = "magnet:?xt=urn:btih:0123456789abcdef0123456789abcdef01234567" +
            "&tr=javascript%3Aalert%281%29" +
            "&tr=https%3A%2F%2Ftracker.example%2Fannounce"

        assertEquals(
            listOf("https://tracker.example/announce"),
            (DeepLinkParser.parse(link) as AppDeepLink.Magnet).trackers
        )
    }
}
