package verendus.leshan.music.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.AudioEffect;
import android.media.audiofx.BassBoost;
import android.media.audiofx.EnvironmentalReverb;
import android.media.audiofx.Equalizer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.RatingCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.IOException;
import java.util.ArrayList;

import verendus.leshan.music.MainActivity;
import verendus.leshan.music.R;
import verendus.leshan.music.objects.God;
import verendus.leshan.music.objects.Song;

/**
 * Created by leshan on 8/14/14.
 */
public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener {

    MediaPlayer mediaPlayer;
    Equalizer equalizer;
    BassBoost bassBoost;
    EnvironmentalReverb environmentalReverb;
    AudioManager audioManager;
    AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener;
    ArrayList<Song> queue;
    int songPosition;
    MusicChanger musicChanger;
    boolean isNewQueue = false;
    MediaSessionCompat mediaSession;
    Notification notification;
    NotificationManager notificationManager;
    private static final int ID = 6900619;
    boolean isInitialized = false;
    private God god;

    private final String ACTION_PLAY_PAUSE = "action.play_pause";
    private final String ACTION_PREVIOUS = "action.previous";
    private final String ACTION_NEXT = "action.next";
    private final String ACTION = "verendus.leshan.music";
    private final String ACTION_STOP = "action.stop";

    private final IBinder musicBind = new MusicBinder();


    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        //mediaPlayer.stop();
        //mediaPlayer.release();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        playNext();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d("ERROR :", "MediaPlayer");
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        musicChanger.onPlay(songPosition, isNewQueue);
        isNewQueue = false;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        registerBroadcastReceivers();

        ComponentName receiver = new ComponentName(getPackageName(), RemoteReceiver.class.getName());
        mediaSession = new MediaSessionCompat(this, "PlayerService", receiver, null);
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setPlaybackState(new PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_PAUSED, 0, 0)
                .setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE |
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_SET_RATING)
                .build());
        mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "Test Artist")
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, "Test Album")
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, "Test Track Name")
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 10000)
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART,
                        BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .build());

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                switch (focusChange) {
                    case AudioManager.AUDIOFOCUS_GAIN:
                        // resume playback
                        if (mediaPlayer == null) initMediaPlayer();
                        else if (!mediaPlayer.isPlaying()) mediaPlayer.start();
                        mediaPlayer.setVolume(1.0f, 1.0f);
                        Toast.makeText(getApplicationContext(), "Audio focus gained", Toast.LENGTH_SHORT).show();
                        break;

                    case AudioManager.AUDIOFOCUS_LOSS:
                        // Lost focus for an unbounded amount of time: stop playback and release media mediaPlayer
                        if (mediaPlayer.isPlaying()) mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = null;
                        isInitialized = false;
                        Toast.makeText(getApplicationContext(), "Audio focus lost", Toast.LENGTH_SHORT).show();
                        break;

                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        // Lost focus for a short time, but we have to stop
                        // playback. We don't release the media mediaPlayer because playback
                        // is likely to resume
                        if (mediaPlayer.isPlaying()) mediaPlayer.pause();
                        break;

                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        // Lost focus for a short time, but it's ok to keep playing
                        // at an attenuated level
                        if (mediaPlayer.isPlaying()) mediaPlayer.setVolume(0.1f, 0.1f);
                        break;
                }
            }
        };
        mediaSession.setActive(true);
        mediaSession.setRatingType(RatingCompat.RATING_HEART);

        songPosition = 0;
        initMediaPlayer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isInitialized = false;
        mediaSession.release();
        mediaPlayer.release();
        equalizer.release();
        bassBoost.release();
        environmentalReverb.release();
        audioManager.abandonAudioFocus(onAudioFocusChangeListener);
        if (notification != null) {
            notificationManager.cancel(ID);
            stopForeground(true);
        }
        if (god != null) god.stopThreads();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (mediaSession.getController().getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING) {
            mediaSession.setPlaybackState(new PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_PAUSED, 0, 0.0f)
                    .setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE).build());
        } else {
            mediaSession.setPlaybackState(new PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_PLAYING, 0, 1.0f)
                    .setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE).build());
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);

        equalizer = new Equalizer(0, mediaPlayer.getAudioSessionId());
        equalizer.setEnabled(true);

        bassBoost = new BassBoost(0, mediaPlayer.getAudioSessionId());
        bassBoost.setEnabled(true);

        environmentalReverb = new EnvironmentalReverb(0, mediaPlayer.getAudioSessionId());
        environmentalReverb.setEnabled(false);

        isInitialized = true;
    }

    public void setQueue(ArrayList<Song> newQueue) {
        queue = newQueue;
        isNewQueue = true;
    }

    public ArrayList<Song> getQueue() {
        return queue;
    }

    public void setSong(int songIndex) {
        songPosition = songIndex;
    }

    public void removeSongFromQueue(int adapterPosition) {
        queue.remove(adapterPosition);
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    public void playSong() {
        Thread newThread = new Thread(new Runnable() {
            @Override
            public void run() {

                if (!isInitialized) initMediaPlayer();
                audioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
                if (mediaPlayer != null) mediaPlayer.reset();
                final Song song = queue.get(songPosition);
                long songID = song.getSongId();

                Uri trackUri = ContentUris.withAppendedId(
                        android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        songID);

                try {
                    mediaPlayer.setDataSource(getApplicationContext(), trackUri);
                } catch (IOException e) {
                    Log.e("MUSIC SERVICE", "Error setting data source", e);

                } catch (Exception e) {
                    Log.e("MUSIC SERVICE", "Error setting data source", e);
                }

                try {
                    mediaPlayer.prepareAsync();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }


                God.getImageLoder(getApplicationContext()).loadImage(song.getCoverArt(), new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, final Bitmap loadedImage) {

                        mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.getArtist())
                                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, song.getAlbumName())
                                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.getTitle())
                                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 10000)
                                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART,
                                        loadedImage)
                                .build());

                        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
                        notificationIntent.setAction(Intent.ACTION_MAIN);
                        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);


                        Intent nextIntent = new Intent(ACTION_NEXT);
                        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, nextIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                        Intent previousIntent = new Intent(ACTION_PREVIOUS);
                        PendingIntent previousPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, previousIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                        Intent playIntent = new Intent(ACTION_PLAY_PAUSE);
                        PendingIntent playPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, playIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                        Intent stopIntent = new Intent(ACTION_STOP);
                        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, stopIntent, PendingIntent.FLAG_CANCEL_CURRENT);


                        notification = new NotificationCompat.Builder(getApplicationContext())
                                .setSmallIcon(R.drawable.ic_now_playing_notification)
                                .setContentTitle(song.getTitle())
                                .setColor(Color.parseColor("#212121"))
                                .setContentText(song.getArtist())
                                .setLargeIcon(loadedImage)
                                .setDeleteIntent(stopPendingIntent)
                                .setContentIntent(pendingIntent)
                                .addAction(R.drawable.ic_previous, "Previous", previousPendingIntent) // #0
                                .addAction(R.drawable.ic_pause, "Pause", playPendingIntent)  // #1
                                .addAction(R.drawable.ic_next, "Next", nextPendingIntent)     // #2
                                .addAction(R.mipmap.ic_queue_music, "Queue", null)
                                .setStyle(new NotificationCompat.MediaStyle()
                                        .setShowActionsInCompactView(1)
                                        .setMediaSession(mediaSession.getSessionToken()))
                                .build();

                        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        notificationManager.notify(ID, notification);
                        startForeground(ID, notification);


                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                });

            }
        });

        newThread.start();

    }


    public void registerBroadcastReceivers() {
        BroadcastReceiver pauseResumeBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                pauseResumeSong();
            }
        };
        BroadcastReceiver previousBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                playPrevious();

            }
        };
        BroadcastReceiver nextBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                playNext();

            }
        };
        BroadcastReceiver becomingNoisy = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                pauseSong();
                Toast.makeText(getApplicationContext(), "Headphones, where art you??", Toast.LENGTH_SHORT).show();
            }
        };
        BroadcastReceiver stopService = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                stopSelf();
            }
        };
        registerReceiver(pauseResumeBroadcastReceiver, new IntentFilter(ACTION_PLAY_PAUSE));
        registerReceiver(previousBroadcastReceiver, new IntentFilter(ACTION_PREVIOUS));
        registerReceiver(nextBroadcastReceiver, new IntentFilter(ACTION_NEXT));
        registerReceiver(stopService, new IntentFilter(ACTION_STOP));
        registerReceiver(becomingNoisy, new IntentFilter(android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY));
    }


    public void pauseSong() {
        mediaPlayer.pause();
        //pausePosition = mediaPlayer.getCurrentPosition();
        stopForeground(false);

    }

    public void resumeSong() {
        mediaPlayer.start();
        //mediaPlayer.seekTo(pausePosition);
        startForeground(ID, notification);
    }

    public void pauseResumeSong() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                pauseSong();
            } else {
                resumeSong();
            }
        }
    }

    public boolean isPlaying() {
        if (!isInitialized) {
            //initMediaPlayer();
            return false;
        } else {
            return mediaPlayer.isPlaying();
        }
    }

    public void playPrevious() {
        if (songPosition == 0) {
            songPosition = queue.size() - 1;
        } else {
            songPosition--;
        }
        playSong();
    }

    public void playNext() {
        songPosition++;
        if (songPosition == queue.size()) songPosition = 0;
        playSong();
    }

    public Equalizer getEqualizer() {
        return equalizer;
    }

    public BassBoost getBassBoost() {
        return bassBoost;
    }

    public int getPosition() {
        return songPosition;
    }

    public void setMusicChanger(MusicChanger musicChanger) {
        this.musicChanger = musicChanger;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public God getGod() {
        return god;
    }

    public void setGod(God god) {
        this.god = god;
    }
}
