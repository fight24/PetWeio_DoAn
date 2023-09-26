//package com.petweio.projectdoan.route;
//
//
//import android.os.Handler;
//import android.os.Looper;
//
//public abstract class  MyAsyncTask<Params, Progress, Result> {
//private Thread workerThread;
//
//public abstract Result doInBackground(Params... params);
//
//public void onPreExecute() {}
//
//public void onPostExecute(Result result) {}
//
//public void onProgressUpdate(Progress... values) {}
//
//public void onCancelled(Result result) {}
//
//public void onCancelled() {}
//
//@SafeVarargs
//public final void execute(final Params... params) {
//        onPreExecute();
//
//        workerThread = new Thread(() -> {
//        final Result result = doInBackground(params);
//                postResult(result);
//                });
//
//        workerThread.start();
//        }
//
//@SafeVarargs
//public final void publishProgress(final Progress... values) {
//        // Implement this method to update UI with progress
//        }
//
//public void cancel(boolean mayInterruptIfRunning) {
//        if (workerThread != null) {
//        workerThread.interrupt();
//        }
//        }
//
//private void postResult(final Result result) {
//        // Post the result to the UI thread
//        new Handler(Looper.getMainLooper()).post(() -> onPostExecute(result));
//        }
//        }