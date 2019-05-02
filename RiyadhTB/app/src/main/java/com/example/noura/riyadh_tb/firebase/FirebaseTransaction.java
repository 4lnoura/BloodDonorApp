package com.example.noura.riyadh_tb.firebase;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

/**
 * This will help display a dialog while loading firebase data
 */

public class FirebaseTransaction {

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private ProgressDialog progressDialog;


    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    /**
     * By default begins with a dialog
     *
     * @param context
     */
    public FirebaseTransaction(Context context) {
        this(context, true);
    }

    /**
     * Set the dialog message to Loading
     *
     * @param context
     * @param withDialog
     */
    public FirebaseTransaction(Context context, boolean withDialog) {
        this(context, "", "Loading...", withDialog);
    }

    public FirebaseTransaction(Context context, String dialogTitle, String dialogMessage, boolean withDialog) {
        // create a progress dialog
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(dialogTitle);
        progressDialog.setMessage(dialogMessage);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        if (withDialog)
            progressDialog.show(); // display the dialog
    }

    /**
     * Get a Firebase database reference with the child name specified
     *
     * @param childName
     * @return
     */
    public FirebaseTransaction child(String childName) {
        databaseReference = databaseReference.child(childName);
        return this;
    }

    /**
     * Sets the value for the current database reference
     *
     * @param value
     */
    public void setValue(Object value) {
        setValue(value, null);
    }

    /**
     * Pass in an onCompletionListener in order to know when the data has been added to firebase
     *
     * @param value
     * @param completionListener
     */
    public void setValue(Object value, final DatabaseReference.CompletionListener completionListener) {
        databaseReference.setValue(value, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                progressDialog.dismiss();
                if (completionListener != null)
                    completionListener.onComplete(databaseError, databaseReference);
            }
        });
    }

    /**
     * Add a child to the current selected node
     *
     * @return
     */
    public FirebaseTransaction push() {
        databaseReference = databaseReference.push();
        return this;
    }

    /**
     * Add a chidl with the specified id
     *
     * @param id
     * @return
     */
    public FirebaseTransaction push(String id) {
        databaseReference = databaseReference.child(id);
        return this;
    }

    /**
     * Se tthe value for the current rreference and listen for completion and value changes
     *
     * @param value
     * @param completionListener
     * @param valueEventListener
     */
    public void setValue(Object value, DatabaseReference.CompletionListener completionListener, ValueEventListener valueEventListener) {
        read(valueEventListener);
        setValue(value, completionListener);
    }

    /**
     * Read a database reference
     *
     * @param valueEventListener
     * @param listen             pass in true if you want to listen to subsequent database changes
     */
    public void read(final ValueEventListener valueEventListener, final boolean listen) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                if (valueEventListener != null)
                    valueEventListener.onDataChange(dataSnapshot);
                if (!listen) {
                    databaseReference.removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                if (valueEventListener != null)
                    valueEventListener.onCancelled(databaseError);

                if (!listen) {
                    databaseReference.removeEventListener(this);
                }
            }
        });
    }

    /**
     * By default listens to subsequent database changes from firebase
     *
     * @param valueEventListener
     */
    public void read(final ValueEventListener valueEventListener) {
        read(valueEventListener, true);
    }

    /**
     * Read children for a specific database reference
     *
     * @param childEventListener
     */
    public void readChildren(final ChildEventListener childEventListener) {
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                if (childEventListener != null)
                    childEventListener.onChildAdded(dataSnapshot, s);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                if (childEventListener != null)
                    childEventListener.onChildChanged(dataSnapshot, s);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                if (childEventListener != null)
                    childEventListener.onChildRemoved(dataSnapshot);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                if (childEventListener != null)
                    childEventListener.onChildMoved(dataSnapshot, s);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                if (childEventListener != null)
                    childEventListener.onCancelled(databaseError);
            }
        });
    }


    /**
     * Uploads an image to firebase storage
     *
     * @param bitmap
     * @param child
     * @param onFailureListener
     */
    public void uploadImage(Bitmap bitmap, final String child, final OnFailureListener onFailureListener, final OnSuccessListener<Uri> onSuccessListener) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        byte[] data = byteArrayOutputStream.toByteArray();

        final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(child); // get reference to firebase storage
        UploadTask uploadTask = storageReference.putBytes(data);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (onFailureListener != null) {
                    onFailureListener.onFailure(e);
                }

                if (progressDialog.isShowing())
                    progressDialog.dismiss();

            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                // if the upload was successful, get the uri of the image
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (onSuccessListener != null) {
                            onSuccessListener.onSuccess(uri);
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                        }
                    }
                });
            }
        });
    }
}

