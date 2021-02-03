//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.hiddenalt.mtbe.graphql;

import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.ApolloCall.Callback;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import org.jetbrains.annotations.NotNull;
import ru.hiddenalt.mtbe.ConnectionTestQuery;
import ru.hiddenalt.mtbe.ConnectionTestQuery.Data;

public class GraphqlClient {
    protected static ApolloClient apolloClient;
    protected static String hostURL = "http://dev.memi4.local/graphql";
    protected static boolean isOnline = false;
    protected static boolean isPendingOnlineStatus = false;

    public GraphqlClient() {
    }

    public static void initialize() {
        rebuild();
    }

    protected static void rebuild() {
        if (hostURL != null && !hostURL.equals("") && !hostURL.trim().equals("")) {
            apolloClient = ApolloClient.builder().serverUrl(hostURL).build();
        }
    }

    public static void onlineTest() {
        onlineTest(new ConnectionCheckCallback() {
            public void success() {
            }

            public void failure() {
            }
        });
    }

    public static void onlineTest(final ConnectionCheckCallback event) {
        isPendingOnlineStatus = true;
        apolloClient.query(new ConnectionTestQuery()).enqueue(new Callback<Data>() {
            public void onResponse(@NotNull Response<Data> response) {
                GraphqlClient.isPendingOnlineStatus = false;
                GraphqlClient.isOnline = true;
                event.success();
            }

            public void onFailure(@NotNull ApolloException e) {
                GraphqlClient.isPendingOnlineStatus = false;
                GraphqlClient.isOnline = false;
                event.failure();
            }
        });
    }

    public static void setHostURL(String url) {
        hostURL = url;
        rebuild();
    }

    public static String getHostURL() {
        return hostURL;
    }

    public static ApolloClient getApolloClient() {
        return apolloClient;
    }

    public static boolean isIsOnline() {
        return isOnline;
    }

    public static boolean isIsPendingOnlineStatus() {
        return isPendingOnlineStatus;
    }
}
