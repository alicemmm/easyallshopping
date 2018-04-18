package com.lomoasia.easyallshopping.activities;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.lomoasia.easyallshopping.R;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.FlowableSubscriber;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by asia on 2018/3/28.
 */

public class RxActivity extends AppCompatActivity {
    private static final String TAG = RxActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx);
        test();
    }

    private void test() {
//        Observer<String> observer = new Observer<String>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//
//            }
//
//            @Override
//            public void onNext(String s) {
//
//            }
//
//            @Override
//            public void onError(Throwable e) {
//
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//        };
//
//        Subscriber<String> subscriber = new Subscriber<String>() {
//            @Override
//            public void onSubscribe(Subscription s) {
//
//            }
//
//            @Override
//            public void onNext(String s) {
//
//            }
//
//            @Override
//            public void onError(Throwable t) {
//
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//        };

//        Observable.create(new ObservableOnSubscribe<String>() {
//            @Override
//            public void subscribe(ObservableEmitter<String> observableEmitter) throws Exception {
//                if (!observableEmitter.isDisposed()) {
//                    observableEmitter.onNext("我是消息");
//                    observableEmitter.onComplete();
//                }
//            }
//        }).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<String>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        Log.e(TAG, "onSubscribe: " + d);
//                    }
//
//                    @Override
//                    public void onNext(String s) {
//                        Log.e(TAG, "onNext: " + s);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.e(TAG, "onError: " + e);
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        Log.e(TAG, "onComplete: ");
//                    }
//                });


//        Observable observable = Observable.just("Hello","Hi","Aloha");
//        observable.subscribe(new Observer() {
//            @Override
//            public void onSubscribe(Disposable d) {
//
//            }
//
//            @Override
//            public void onNext(Object o) {
//                Log.e(TAG, "onNext 2: " + o);
//            }
//
//            @Override
//            public void onError(Throwable e) {
//
//            }
//
//            @Override
//            public void onComplete() {
//                Log.e(TAG, "onComplete: ");
//            }
//        });

        //创建观察者
        FlowableSubscriber<String> subscriber = new FlowableSubscriber<String>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(Long.MAX_VALUE); //请求多少事件，表示不限制
            }

            @Override
            public void onNext(String s) {
                Log.e(TAG, "onNext: " + s);
            }

            @Override
            public void onError(Throwable t) {
                Log.e(TAG, "onError: " + t);
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "onComplete: ");
            }
        };

        //被观察者
        Flowable<String> flowable = Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(FlowableEmitter<String> e) throws Exception {
                //订阅观察者的操作
                e.onNext("test1");
                e.onNext("test2");
                e.onComplete();

            }
        }, BackpressureStrategy.BUFFER);

        flowable.subscribe(subscriber);

//        flowable.subscribe(new Consumer<String>() {
//            @Override
//            public void accept(String s) throws Exception {
//                Log.e(TAG, "accept: "+s);
//            }
//        });

        /**
         *subscribe(onNext)
         *subscribe(onNext,onError)
         *subscribe(onNext,onError,onComplete)
         *subscribe(onNext,onError,onComplete,onSubscribe)
         *
         *
         * Action无参数类型
         * Consumer<T> 有参数类型
         * BiConsumer<T1，T2>双参数类型
         * Consumer<Object>
         *
         * */


//        Flowable.just("test1","test2").subscribe(str-> Log.e(TAG, "test: "+str));
//
//        Flowable.fromArray(1,2,3,4,5).subscribe(integer -> Log.e(TAG, "test: "+integer));

//        Observable.just(1,2,3).filter(new Predicate<Integer>() {
//            @Override
//            public boolean test(Integer integer) throws Exception {
//                return integer>=2;
//            }
//        }).subscribe(new Consumer<Integer>() {
//            @Override
//            public void accept(Integer integer) throws Exception {
//                Log.e(TAG, "accept: "+integer);
//            }
//        });
//
//        Flowable.just("a","b","c")
//                .skip(1)
//                .skipLast(1)
//                .subscribe(ele-> Log.e(TAG, "test: "+ele ));

//        Flowable.just(1, 2, 3)
//                .map(integer -> "int" + integer)
//                .subscribe(ele -> Log.e(TAG, "test: " + ele));
//
//        Flowable.just(1, 2, 3)
//                .flatMap((Function<Integer, Publisher<?>>)
//                        integer -> Flowable.just("a", integer))
//                .subscribe(ele -> Log.e(TAG, "test: " + ele));

//        Observable.create(new ObservableOnSubscribe<String>() {
//            @Override
//            public void subscribe(ObservableEmitter<String> e) throws Exception {
//                e.onNext(getFilePath());
//            }
//        })
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(Schedulers.io())
//                .map(new Function<String, Bitmap>() {
//                    @Override
//                    public Bitmap apply(String s) throws Exception {
//                        return createBitmapFromPath(s);
//                    }
//                })
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<Bitmap>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(Bitmap bitmap) {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });


    }

    private String getFilePath() {
        return "";
    }

    private Bitmap createBitmapFromPath(String path) {
        return BitmapFactory.decodeFile(path);
    }
}
