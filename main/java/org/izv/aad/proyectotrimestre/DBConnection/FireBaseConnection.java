package org.izv.aad.proyectotrimestre.DBConnection;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.content.SharedPreferences.Editor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.izv.aad.proyectotrimestre.Activities.MainActivity;
import org.izv.aad.proyectotrimestre.POJO.Author;
import org.izv.aad.proyectotrimestre.POJO.Readings;

import java.util.HashMap;
import java.util.Map;

import static org.izv.aad.proyectotrimestre.Activities.MainActivity.TAG;

public class FireBaseConnection {
    public static FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    public static FirebaseStorage storage;
    public static StorageReference storageRef;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public FireBaseConnection(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }
    public DatabaseReference getDatabaseReference(String path) {
        return firebaseDatabase.getReference(path);
    }

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }


    public String getEmail(){
        return firebaseUser.getEmail();
    }

    public FirebaseUser signIn(String email, String password/*, Activity activity*/) {
        Log.v(TAG, "40");
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.v(TAG, "46");
                                if (task.isSuccessful()) {
                                    Log.v(TAG, "48");
                                    firebaseUser = firebaseAuth.getCurrentUser();

                                   Log.v(TAG, firebaseUser.getEmail());
                                } else {
                                    Log.v(TAG, "54");
                                    Log.v(TAG, task.getException().toString() );
                                }
                            }
                        });
        return firebaseAuth.getCurrentUser();
    }

    public FirebaseUser createUser(String email, String password) {
        Log.v(TAG, "62");
        firebaseAuth.createUserWithEmailAndPassword(email, password).
                addOnCompleteListener(
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    firebaseUser = firebaseAuth.getCurrentUser();
                                    firebaseUser = task.getResult().getUser();
                                    Log.v(TAG, "71");
                                } else {
                                    Log.v(TAG, "87 "+task.getException().toString());
                                }
                            }
                        });
        return firebaseUser;
    }

    public void saveUser() {
        Map<String, Object> saveUser = new HashMap<>();
        saveUser.put("/user/" + firebaseUser.getUid(), firebaseUser.getEmail());
        databaseReference.updateChildren(saveUser).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.v(TAG, task.getResult().toString());
                        } else {
                            Log.v(TAG, task.getException().toString());
                        }
                    }
                });
    }

    public void saveReading(Readings reading) {
        Map<String, Object> saveUser = new HashMap<>(); // SIEMPRE ES NULL LA FIREBASE KEY, HAY Q VER POR QUE
        String fireBaseKey = reading.getFireBaseKey();
        saveUser.put("/user/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/readings/" + fireBaseKey + "/", reading.toMap());
        databaseReference.updateChildren(saveUser).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.v(TAG, "SI INSERTO READING");
                        } else {
                            Log.v(TAG, "NO INSERTO READING");
                        }
                    }
                });
    }

    public void saveAuthor(Author author) {
        Map<String, Object> saveUser = new HashMap<>();
        saveUser.put("/user/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/authors/" + author.getFireBaseKey() + "/", author.toMap());
        databaseReference.updateChildren(saveUser).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.v(TAG, "SI INSERTO AUTOR");
                        } else {
                            Log.v(TAG, "NO INSERTO AUTOR");
                        }
                    }
                });
    }

    public void deleteReading(Readings reading){
        databaseReference.child("/user/" + firebaseAuth.getCurrentUser().getUid() + "/readings/" + reading.getFireBaseKey()).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                //Log.v(TAG, databaseError.toString());
            }
        });
    }

    public void deleteAuthor(Author author){
        databaseReference.child("/user/" + firebaseAuth.getCurrentUser().getUid() + "/authors/" + author.getFireBaseKey()).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                //Log.v(TAG, databaseError.toString());
            }
        });
    }

    public void signOut(){
        firebaseAuth.signOut();
    }
}
