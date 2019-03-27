package com.example.moneymanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moneymanager.DAO.AppDatabase;
import com.example.moneymanager.DAO.Category;
import com.example.moneymanager.DAO.ExpensesAndIncomes;
import com.example.moneymanager.DAO.TimeStamp;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
//import com.google.firebase.quickstart.auth.R;

public class EmailPasswordActivity extends BaseActivity implements
        View.OnClickListener {

    private static final String TAG = "EmailPassword";
    private static final int RC_SIGN_IN = 9001;

    private TextView mStatusTextView;
    private TextView mDetailTextView;
    private EditText mEmailField;
    private EditText mPasswordField;
    private GoogleSignInClient mGoogleSignInClient;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore firedb;
    private AppDatabase db;

    // [END declare_auth]

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_password);

        db = AppDatabase.getInstance(this);
        firedb = FirebaseFirestore.getInstance();

        // Views
        mStatusTextView = findViewById(R.id.status);
        mDetailTextView = findViewById(R.id.detail);
        mEmailField = findViewById(R.id.fieldEmail);
        mPasswordField = findViewById(R.id.fieldPassword);

        // Buttons
        findViewById(R.id.emailSignInButton).setOnClickListener(this);
        findViewById(R.id.emailCreateAccountButton).setOnClickListener(this);
        findViewById(R.id.signOutButton).setOnClickListener(this);
        findViewById(R.id.verifyEmailButton).setOnClickListener(this);
        findViewById(R.id.signInButtonGoogle).setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
    }
    // [END on_start_check_user]

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // [START_EXCLUDE]
                // [END_EXCLUDE]
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            currentUser = mAuth.getCurrentUser();
                            syncBase();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_google]


    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            currentUser = mAuth.getCurrentUser();
                            syncBase();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
                            mStatusTextView.setText(R.string.auth_failed);
                        }
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }

    private void sendEmailVerification() {
        // Disable button
        findViewById(R.id.verifyEmailButton).setEnabled(false);

        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        // Re-enable button
                        findViewById(R.id.verifyEmailButton).setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(EmailPasswordActivity.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(EmailPasswordActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END send_email_verification]
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }


    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.emailCreateAccountButton) {
            createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.emailSignInButton) {
            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.signOutButton) {
        } else if (i == R.id.verifyEmailButton) {
            sendEmailVerification();
        } else if (i == R.id.signInButtonGoogle) {
            signInWithGoogle();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        moveTaskToBack(true);
    }

    public void syncBase(){
        if (currentUser != null) {
            firedb.collection("Categories").document(currentUser.getEmail())
                    .collection("UserCategoriesExpenses").document("time").get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String strTime = (String) documentSnapshot.get("time");
                            TimeStamp timeStamp = db.timeStampDAO().getCategoryTime();
                            if (timeStamp == null) {
                                firedb.collection("Categories").document(currentUser.getEmail())
                                        .collection("UserCategoriesExpenses").get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                                    Category category = document.toObject(Category.class);
                                                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                                        @Override
                                                        public void run() {

                                                        }
                                                    });
                                                    db.categoryDAO().addCategory(category);
                                                }
                                            }
                                        });
                                TimeStamp obj = new TimeStamp(strTime);
                                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                    @Override
                                    public void run() {

                                    }
                                });
                                db.timeStampDAO().addTimeStamp(obj);
                            } else {
                                if (timeStamp.getTimeCategory() != null && strTime != null) {
                                    if (!strTime.equals(timeStamp.getTimeCategory())) {
                                        List<Integer> idList = new ArrayList<>();
                                        List<Integer> idCloud = new ArrayList<>();
                                        firedb.collection("Categories").document(currentUser.getEmail())
                                                .collection("UserCategoriesExpenses").get()
                                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                        List<Integer> idList = new ArrayList<>();
                                                        List<Integer> idCloud = new ArrayList<>();
                                                        List<Category> list = db.categoryDAO().loadExpenses();
                                                        for (int i = 0; i < list.size(); i++) {
                                                            idList.add(list.get(i).getId());
                                                        }
                                                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                                            Category category = document.toObject(Category.class);
                                                            idCloud.add(category.getId());
                                                            if (!idList.contains(category.getId())) {
                                                                Category newCat = new Category(category.getName(), category.getPhoto(), category.getType());
                                                                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                                                    @Override
                                                                    public void run() {

                                                                    }
                                                                });
                                                                db.categoryDAO().addCategory(newCat);
                                                            }
                                                        }
                                                        if (idCloud.contains(0)){
                                                            for (int i = 0; i < idCloud.size(); i++) {
                                                                if (idCloud.get(i) == 0){
                                                                    idCloud.remove(idCloud.get(i));
                                                                }
                                                            }
                                                        }
                                                        if (idList.size() > idCloud.size()){
                                                            for (int i = 0; i < idList.size(); i++) {
                                                                if (!idCloud.contains(idList.get(i))){
                                                                    Category category = db.categoryDAO().findById(idList.get(i));
                                                                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                                                        @Override
                                                                        public void run() {

                                                                        }
                                                                    });
                                                                    db.categoryDAO().deleteCateogry(category);
                                                                }
                                                            }
                                                        }
                                                    }
                                                });

                                        timeStamp.setTimeCategory(strTime);
                                        timeStamp.setId(timeStamp.getId());
                                        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                            @Override
                                            public void run() {

                                            }
                                        });
                                        db.timeStampDAO().edit(timeStamp);
                                    }
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.out.println(e);
                        }
                    });
        }
        syncCategoryIncome();
        syncExpenses();
        syncIncomes();
    }

    private void syncCategoryIncome() {
        if (currentUser != null) {
            firedb.collection("Categories").document(currentUser.getEmail())
                    .collection("UserCategoryIncomes").document("time").get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String strTime = (String) documentSnapshot.get("time");
                            final TimeStamp timeStamp = db.timeStampDAO().getCategoryTime();
                            if (timeStamp != null) {
                                if (timeStamp.getTimeCategoryIncome() == null) {
                                    firedb.collection("Categories").document(currentUser.getEmail())
                                            .collection("UserCategoryIncomes").get()
                                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                                        Category category = document.toObject(Category.class);
                                                        final Category newCat = new Category(category.getName(), category.getPhoto(), category.getType());
                                                        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                db.categoryDAO().addCategory(newCat);
                                                            }
                                                        });

                                                    }
                                                }
                                            });
                                    timeStamp.setTimeCategoryIncome(strTime);
                                    timeStamp.setId(timeStamp.getId());
                                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            db.timeStampDAO().edit(timeStamp);
                                        }
                                    });

                                } else {
                                    if (timeStamp.getTimeCategoryIncome() != null && strTime != null) {
                                        if (!strTime.equals(timeStamp.getTimeCategoryIncome())) {
                                            List<Integer> idList = new ArrayList<>();
                                            List<Integer> idCloud = new ArrayList<>();
                                            firedb.collection("Categories").document(currentUser.getEmail())
                                                    .collection("UserCategoryIncomes").get()
                                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                            List<Integer> idList = new ArrayList<>();
                                                            List<Integer> idCloud = new ArrayList<>();
                                                            List<Category> list = db.categoryDAO().loadIncomes();
                                                            for (int i = 0; i < list.size(); i++) {
                                                                idList.add(list.get(i).getId());
                                                            }
                                                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                                                Category category = document.toObject(Category.class);
                                                                idCloud.add(category.getId());
                                                                if (!idList.contains(category.getId())) {
                                                                    final Category newCat = new Category(category.getName(), category.getPhoto(), category.getType());
                                                                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            db.categoryDAO().addCategory(newCat);
                                                                        }
                                                                    });

                                                                }
                                                            }
                                                            if (idCloud.contains(0)) {
                                                                for (int i = 0; i < idCloud.size(); i++) {
                                                                    if (idCloud.get(i) == 0) {
                                                                        idCloud.remove(idCloud.get(i));
                                                                    }
                                                                }
                                                            }
                                                            if (idList.size() > idCloud.size()) {
                                                                for (int i = 0; i < idList.size(); i++) {
                                                                    if (!idCloud.contains(idList.get(i))) {
                                                                        final Category category = db.categoryDAO().findById(idList.get(i));
                                                                        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                db.categoryDAO().deleteCateogry(category);
                                                                            }
                                                                        });

                                                                    }
                                                                }
                                                            }
                                                        }
                                                    });

                                            timeStamp.setTimeCategory(strTime);
                                            timeStamp.setId(timeStamp.getId());
                                            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                                @Override
                                                public void run() {
                                                    db.timeStampDAO().edit(timeStamp);
                                                }
                                            });

                                        }
                                    }
                                }
                            }
                        }
                    });
        }
    }

    private void syncExpenses(){
        if (currentUser != null) {
            firedb.collection("Expenses").document(currentUser.getEmail())
                    .collection("Expenses").document("time").get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String strTime = (String) documentSnapshot.get("time");
                            final TimeStamp timeStamp = db.timeStampDAO().getCategoryTime();
                            if (timeStamp != null) {
                                if (timeStamp.getTimeExpenses() == null) {
                                    firedb.collection("Expenses").document(currentUser.getEmail())
                                            .collection("Expenses").get()
                                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                                        ExpensesAndIncomes item = document.toObject(ExpensesAndIncomes.class);
                                                        final ExpensesAndIncomes expenses = new ExpensesAndIncomes(item.getNote(), item.getPrice(), item.getDate(), item.getType(), item.getCategory());
                                                        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                db.expensesAndIncomeDAO().addNew(expenses);
                                                            }
                                                        });

                                                    }
                                                }
                                            });
                                    timeStamp.setTimeExpenses(strTime);
                                    timeStamp.setId(timeStamp.getId());
                                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            db.timeStampDAO().edit(timeStamp);
                                        }
                                    });

                                } else {
                                    if (timeStamp != null) {
                                        if (timeStamp.getTimeExpenses() != null && strTime != null) {
                                            if (!strTime.equals(timeStamp.getTimeExpenses())) {
                                                List<Integer> idList = new ArrayList<>();
                                                List<Integer> idCloud = new ArrayList<>();
                                                firedb.collection("Expenses").document(currentUser.getEmail())
                                                        .collection("Expenses").get()
                                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                List<Integer> idList = new ArrayList<>();
                                                                List<Integer> idCloud = new ArrayList<>();
                                                                List<ExpensesAndIncomes> list = db.expensesAndIncomeDAO().getExpenses();
                                                                for (int i = 0; i < list.size(); i++) {
                                                                    idList.add(list.get(i).getId());
                                                                }
                                                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                                                    ExpensesAndIncomes item = document.toObject(ExpensesAndIncomes.class);
                                                                    idCloud.add(item.getId());
                                                                    if (!idList.contains(item.getId())) {
                                                                        final ExpensesAndIncomes newItem = new ExpensesAndIncomes(item.getNote(), item.getPrice(), item.getDate(), item.getType(), item.getCategory());
                                                                        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                db.expensesAndIncomeDAO().addNew(newItem);
                                                                            }
                                                                        });

                                                                    }
                                                                }
                                                                if (idCloud.contains(0)) {
                                                                    for (int i = 0; i < idCloud.size(); i++) {
                                                                        if (idCloud.get(i) == 0) {
                                                                            idCloud.remove(idCloud.get(i));
                                                                        }
                                                                    }
                                                                }
                                                                if (idList.size() > idCloud.size()) {
                                                                    for (int i = 0; i < idList.size(); i++) {
                                                                        if (!idCloud.contains(idList.get(i))) {
                                                                            final ExpensesAndIncomes id = db.expensesAndIncomeDAO().findById(idList.get(i));
                                                                            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                                                                @Override
                                                                                public void run() {
                                                                                    db.expensesAndIncomeDAO().delete(id);
                                                                                }
                                                                            });

                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        });

                                                timeStamp.setTimeExpenses(strTime);
                                                timeStamp.setId(timeStamp.getId());
                                                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        db.timeStampDAO().edit(timeStamp);
                                                    }
                                                });

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    });
        }
    }

    private void syncIncomes(){
        if (currentUser != null) {
            firedb.collection("Expenses").document(currentUser.getEmail())
                    .collection("Incomes").document("time").get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String strTime = (String) documentSnapshot.get("time");
                            final TimeStamp timeStamp = db.timeStampDAO().getCategoryTime();
                            if (timeStamp != null) {
                                if (timeStamp.getTimeIncomes() == null) {
                                    firedb.collection("Expenses").document(currentUser.getEmail())
                                            .collection("Incomes").get()
                                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                                        ExpensesAndIncomes item = document.toObject(ExpensesAndIncomes.class);
                                                        final ExpensesAndIncomes expenses = new ExpensesAndIncomes(item.getNote(), item.getPrice(), item.getDate(), item.getType(), item.getCategory());
                                                        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                db.expensesAndIncomeDAO().addNew(expenses);
                                                            }
                                                        });

                                                    }
                                                }
                                            });
                                    List<ExpensesAndIncomes> list = db.expensesAndIncomeDAO().getIncome();
                                    for (int i = 0; i < list.size(); i++) {
                                        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
                                        System.out.println(list.get(i).getNote());
                                        System.out.println(list.get(i).getDate());
                                    }

                                    timeStamp.setTimeIncomes(strTime);
                                    timeStamp.setId(timeStamp.getId());
                                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            db.timeStampDAO().edit(timeStamp);
                                        }
                                    });

                                } else {
                                    if (timeStamp.getTimeIncomes() != null && strTime != null) {
                                        if (!strTime.equals(timeStamp.getTimeIncomes())) {
                                            List<Integer> idList = new ArrayList<>();
                                            List<Integer> idCloud = new ArrayList<>();
                                            firedb.collection("Expenses").document(currentUser.getEmail())
                                                    .collection("Incomes").get()
                                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                            List<Integer> idList = new ArrayList<>();
                                                            List<Integer> idCloud = new ArrayList<>();
                                                            List<ExpensesAndIncomes> list = db.expensesAndIncomeDAO().getExpenses();
                                                            for (int i = 0; i < list.size(); i++) {
                                                                idList.add(list.get(i).getId());
                                                            }
                                                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                                                ExpensesAndIncomes item = document.toObject(ExpensesAndIncomes.class);
                                                                idCloud.add(item.getId());
                                                                if (!idList.contains(item.getId())) {
                                                                    final ExpensesAndIncomes newItem = new ExpensesAndIncomes(item.getNote(), item.getPrice(), item.getDate(), item.getType(), item.getCategory());
                                                                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            db.expensesAndIncomeDAO().addNew(newItem);
                                                                        }
                                                                    });

                                                                }
                                                            }
                                                            if (idCloud.contains(0)) {
                                                                for (int i = 0; i < idCloud.size(); i++) {
                                                                    if (idCloud.get(i) == 0) {
                                                                        idCloud.remove(idCloud.get(i));
                                                                    }
                                                                }
                                                            }
                                                            if (idList.size() > idCloud.size()) {
                                                                for (int i = 0; i < idList.size(); i++) {
                                                                    if (!idCloud.contains(idList.get(i))) {
                                                                        final ExpensesAndIncomes id = db.expensesAndIncomeDAO().findById(idList.get(i));
                                                                        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                db.expensesAndIncomeDAO().delete(id);
                                                                            }
                                                                        });

                                                                    }
                                                                }
                                                            }
                                                        }
                                                    });

                                            timeStamp.setTimeIncomes(strTime);
                                            timeStamp.setId(timeStamp.getId());
                                            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                                @Override
                                                public void run() {
                                                    db.timeStampDAO().edit(timeStamp);
                                                }
                                            });

                                        }
                                    }
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
                            System.out.println(e);
                        }
                    });
        }

    }

}
