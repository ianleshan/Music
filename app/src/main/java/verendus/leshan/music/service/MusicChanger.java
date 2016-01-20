package verendus.leshan.music.service;

/**
 * Created by leshan on 8/15/14.
 */
public interface MusicChanger {
    public void onChange(int position);
    public void onPlay(int position, boolean isNewQueue);
    public void onPrevious(int position);
    public void onNext(int position);
}
