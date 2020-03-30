/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.exoplayer2;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

import android.net.Uri;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.google.android.exoplayer2.offline.StreamKey;
import com.google.android.exoplayer2.util.MimeTypes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;

/** Unit test for {@link MediaItem MediaItems}. */
@RunWith(AndroidJUnit4.class)
public class MediaItemTest {

  private static final String URI_STRING = "http://www.google.com";

  @Test
  public void builder_needsSourceUriOrMediaId() {
    assertThrows(NullPointerException.class, () -> new MediaItem.Builder().build());
  }

  @Test
  public void builderWithUri_setsSourceUri() {
    Uri uri = Uri.parse(URI_STRING);

    MediaItem mediaItem = MediaItem.fromUri(uri);

    assertThat(mediaItem.playbackProperties.sourceUri.toString()).isEqualTo(URI_STRING);
    assertThat(mediaItem.mediaId).isEqualTo(URI_STRING);
    assertThat(mediaItem.mediaMetadata).isNotNull();
  }

  @Test
  public void builderWithUriAsString_setsSourceUri() {
    MediaItem mediaItem = MediaItem.fromUri(URI_STRING);

    assertThat(mediaItem.playbackProperties.sourceUri.toString()).isEqualTo(URI_STRING);
    assertThat(mediaItem.mediaId).isEqualTo(URI_STRING);
  }

  @Test
  public void builderSetMimeType_isNullByDefault() {
    MediaItem mediaItem = MediaItem.fromUri(URI_STRING);

    assertThat(mediaItem.playbackProperties.mimeType).isNull();
  }

  @Test
  public void builderSetMimeType_setsMimeType() {
    MediaItem mediaItem =
        new MediaItem.Builder()
            .setSourceUri(URI_STRING)
            .setMimeType(MimeTypes.APPLICATION_MPD)
            .build();

    assertThat(mediaItem.playbackProperties.mimeType).isEqualTo(MimeTypes.APPLICATION_MPD);
  }

  @Test
  public void builderSetDrmConfig_isNullByDefault() {
    // Null value by default.
    MediaItem mediaItem = new MediaItem.Builder().setSourceUri(URI_STRING).build();
    assertThat(mediaItem.playbackProperties.drmConfiguration).isNull();
  }

  @Test
  public void builderSetDrmConfig_setsAllProperties() {
    Uri licenseUri = Uri.parse(URI_STRING);
    Map<String, String> requestHeaders = new HashMap<>();
    requestHeaders.put("Referer", "http://www.google.com");
    MediaItem mediaItem =
        new MediaItem.Builder()
            .setSourceUri(URI_STRING)
            .setDrmUuid(C.WIDEVINE_UUID)
            .setDrmLicenseUri(licenseUri)
            .setDrmLicenseRequestHeaders(requestHeaders)
            .setDrmMultiSession(/* multiSession= */ true)
            .build();

    assertThat(mediaItem.playbackProperties.drmConfiguration).isNotNull();
    assertThat(mediaItem.playbackProperties.drmConfiguration.uuid).isEqualTo(C.WIDEVINE_UUID);
    assertThat(mediaItem.playbackProperties.drmConfiguration.licenseUri).isEqualTo(licenseUri);
    assertThat(mediaItem.playbackProperties.drmConfiguration.requestHeaders)
        .isEqualTo(requestHeaders);
    assertThat(mediaItem.playbackProperties.drmConfiguration.multiSession).isTrue();
  }

  @Test
  public void builderSetDrmUuid_notCalled_throwsIllegalStateException() {
    assertThrows(
        IllegalStateException.class,
        () ->
            new MediaItem.Builder()
                .setSourceUri(URI_STRING)
                // missing uuid
                .setDrmLicenseUri(Uri.parse(URI_STRING))
                .build());
  }

  @Test
  public void builderSetStreamKeys_setsStreamKeys() {
    List<StreamKey> streamKeys = new ArrayList<>();
    streamKeys.add(new StreamKey(1, 0, 0));
    streamKeys.add(new StreamKey(0, 1, 1));

    MediaItem mediaItem =
        new MediaItem.Builder().setSourceUri(URI_STRING).setStreamKeys(streamKeys).build();

    assertThat(mediaItem.playbackProperties.streamKeys).isEqualTo(streamKeys);
  }

  @Test
  public void builderSetTag_isNullByDefault() {
    MediaItem mediaItem = new MediaItem.Builder().setSourceUri(URI_STRING).build();

    assertThat(mediaItem.playbackProperties.tag).isNull();
  }

  @Test
  public void builderSetTag_setsTag() {
    Object tag = new Object();

    MediaItem mediaItem = new MediaItem.Builder().setSourceUri(URI_STRING).setTag(tag).build();

    assertThat(mediaItem.playbackProperties.tag).isEqualTo(tag);
  }

  @Test
  public void builderSetMediaMetadata_setsMetadata() {
    MediaMetadata mediaMetadata = new MediaMetadata.Builder().setTitle("title").build();

    MediaItem mediaItem =
        new MediaItem.Builder().setSourceUri(URI_STRING).setMediaMetadata(mediaMetadata).build();

    assertThat(mediaItem.mediaMetadata).isEqualTo(mediaMetadata);
  }
}