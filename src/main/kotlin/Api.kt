
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

object Api {

    @Serializable
    private data class SearchResult(val entities: Entities)

    @Serializable
    private data class Entities(val tracks: Map<String, Track>)

    @Serializable
    data class Track(
        val id: Int,
        val kosmosId: String,
        val title: String,
        val added: String,
        val creatives: Creatives,
        val length: Int,
        val durationMs: Int,
        val bpm: Int,
        val isSfx: Boolean,
        val hasVocals: Boolean,
        val hidden: Boolean,
        val publicSlug: String,
        val genres: List<Genre>,
        val moods: List<Mood>,
        val energyLevel: String,
        val stems: Stems,
        val oldTitle: String? = null,
        val seriesId: Int? = null,
        val metadataTags: List<String>,
        val isExplicit: Boolean,
        val isCommercialRelease: Boolean,
        val imageUrl: String,
        val coverArt: CoverArt,
        val releaseDate: String,
        val segmentGroups: List<String>
    )

    @Serializable
    data class Creatives(
        val composers: List<Creative>,
        val mainArtists: List<Creative>,
        val featuredArtists: List<Creative>,
        val producers: List<Creative>
    )

    @Serializable
    data class Creative(
        val creativeType: String,
        val name: String,
        val slug: String
    )

    @Serializable
    data class Genre(
        val tag: String,
        val fatherTag: String? = null,
        val displayTag: String,
        val slug: String
    )

    @Serializable
    data class Mood(
        val tag: String,
        val fatherTag: String,
        val displayTag: String,
        val slug: String
    )

    @Serializable
    data class Stems(
        val full: Stem? = null,
        val bass: Stem? = null,
        val drums: Stem? = null,
        val instruments: Stem? = null,
        val melody: Stem? = null
    )

    @Serializable
    data class Stem(
        val stemType: String,
        val s3TrackId: Int,
        val lqMp3Url: String,
        val waveformUrl: String
    )

    @Serializable
    data class CoverArt(
        val baseUrl: String,
        val sizes: Map<String, String>
    )

    private enum class SearchOption {
        SEARCH_MUSIC,
        SEARCH_SFX,
        ARTIST,
        SFX,
        MUSIC
    }

    private val json = Json {
        ignoreUnknownKeys = true
    }

//    private val url = mapOf(
//        SearchOption.SFX to { mood: String ->
//            "https://www.epidemicsound.com/json/search/sfx/?limit=10&order=desc&sort=relevance&term=${mood.replace(" ", "%20")}"
//        },
//        SearchOption.MUSIC to { track: String ->
//            "https://www.epidemicsound.com/json/track/${track.replace(" ", "-")}"
//        }
//    )

    fun searchArtist(artist: String): List<Track>? {
        val url = "https://www.epidemicsound.com/json/releases/${artist.replace(" ", "-")}"
        val json = fetchJson(url)
        return json?.entities?.tracks?.values?.toList() ?: return null
    }

    fun searchMusic(term: String, limit: Int): List<Track>? {
        val url = "https://www.epidemicsound.com/json/search/tracks/?term=${term.replace(" ", "%20")}&translate_text=false&order=desc&sort=relevance&limit=$limit"
        val json = fetchJson(url)
        val music = json?.entities?.tracks?.values?.toList() ?: return null
        return music
    }

    fun searchSfx(term: String, limit: Int): List<Track>? {
        val url = "https://www.epidemicsound.com/json/search/sfx/?term=${term.replace(" ", "%20")}&translate_text=false&order=desc&sort=relevance&limit=$limit"
        val json = fetchJson(url)
        val sfx = json?.entities?.tracks?.values?.toList() ?: return null
        return sfx
    }

    fun download(track: Track) {
        downloadFile(track.stems.full!!.lqMp3Url, track.title + ".mp3")
    }

    fun play(track: Track) {
        val filepath = downloadFile(track.stems.full!!.lqMp3Url, track.title + ".mp3", "play")
        Runtime.getRuntime().exec("open $filepath")
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun fetchJson(url: String): SearchResult? {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        client.newCall(request).execute().use { response ->
            return json.decodeFromString<SearchResult>(response.body?.string() ?: return null)
        }
    }

    private fun downloadFile(url: String, filename: String, subFolder: String = ""): String {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        client.newCall(request).execute().use { response ->
            val file = File(System.getProperty("user.home") + "/Downloads/" + subFolder, filename)
            file.writeBytes(response.body?.bytes() ?: throw Exception("Failed to download file"))
            return file.absolutePath
        }
    }

}