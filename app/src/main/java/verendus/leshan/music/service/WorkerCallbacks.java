package verendus.leshan.music.service;

import verendus.leshan.music.objects.God;

/**
 * Created by leshan on 1/24/16.
 */
public interface WorkerCallbacks {

    public void postExecute(God god);
    public void preExecute();
}
