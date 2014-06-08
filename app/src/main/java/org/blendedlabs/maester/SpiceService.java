package org.blendedlabs.maester;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.binary.InFileBitmapObjectPersister;
import com.octo.android.robospice.persistence.binary.InFileInputStreamObjectPersister;
import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.persistence.memory.LruCacheBitmapObjectPersister;
import com.octo.android.robospice.persistence.retrofit.RetrofitObjectPersisterFactory;
import com.octo.android.robospice.retrofit.RetrofitGsonSpiceService;

import roboguice.util.temp.Ln;

public class SpiceService extends RetrofitGsonSpiceService {

    public static final Uri baseUrl = Uri.parse("https://raw.githubusercontent.com/ashtuchkin/maester-courses/master/");

    @Override
    protected String getServerUrl() {
        return baseUrl.toString();
    }

    @Override
    public void onCreate() {
        Ln.getConfig().setLoggingLevel(Log.ERROR);
        super.onCreate();
        addRetrofitInterface(CourseModel.class);
    }

    @Override
    public CacheManager createCacheManager(Application application) throws CacheCreationException {
        CacheManager manager = new CacheManager();

        InFileInputStreamObjectPersister filePersister = new InFileInputStreamObjectPersister(getApplication());
        filePersister.setAsyncSaveEnabled(true);
        manager.addPersister(filePersister);

        InFileBitmapObjectPersister imgFilePersister = new InFileBitmapObjectPersister(getApplication());
        LruCacheBitmapObjectPersister imgMemoryPersister = new LruCacheBitmapObjectPersister(imgFilePersister, 100 * 1024 * 1024);
        manager.addPersister(imgMemoryPersister);

        // Order is important: by default, Retrofit persists all objects.
        manager.addPersister(new RetrofitObjectPersisterFactory(application, getConverter(), getCacheFolder()));
        return manager;
    }

    @Override
    public int getThreadCount() {
        return 8;
    }

    @Override
    public int getThreadPriority() {
        return Thread.NORM_PRIORITY;
    }
}
