package service;
import java.util.UUID;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.MySqlAuthDAO;
import dataaccess.MySqlGameDAO;
import dataaccess.MySqlUserDAO;
import dataaccess.UserDAO;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import requests.Request;
import results.Result;


public class ChessService {

    private final AuthDAO authAccess;
    private final GameDAO gameAccess;
    private final UserDAO userAccess;
    
    public ChessService(AuthDAO authAccess, GameDAO gameAccess, UserDAO userAccess){
        this.authAccess = authAccess;
        this.gameAccess = gameAccess;
        this.userAccess = userAccess;
    }

    public ChessService() throws ResponseException{
        authAccess = new MySqlAuthDAO();
        gameAccess = new MySqlGameDAO();
        userAccess = new MySqlUserDAO();        
    }

    public void clear(Request.Delete clearDatabaseRequest) throws ResponseException{
        if(authAccess == null || gameAccess == null || userAccess == null){
            throw new ResponseException(500, "Error: databases aren't initalized");
        }
        // clear authDAO
        authAccess.clear();
        // clear userDAO
        gameAccess.clear();
        // clear gameDAO
        userAccess.clear();
    }

    public Result.GetGames showGames(Request.GetGames showGameRequest) throws ResponseException{
        checkAuth(showGameRequest.authToken());
        return new Result.GetGames(gameAccess.listGames());
    }

    public Result.CreateGame createGame(String authToken, Request.CreateGame createGameRequest) throws ResponseException{
        checkAuth(authToken);
        return new Result.CreateGame(gameAccess.createGame(createGameRequest.gameName()));
    }

    public void joinGame(String authToken, Request.JoinGame joinGameRequest) throws ResponseException{
        if(joinGameRequest.gameID() < 1){
            throw new ResponseException(400, "Error: bad request -- invalid game");
        }
        if(joinGameRequest.playerColor() == null || joinGameRequest.playerColor().isEmpty()){
            throw new ResponseException(400, "Error: bad request -- invalid player color");
        }
        checkAuth(authToken);
        GameData game = gameAccess.getGame(joinGameRequest.gameID());
        if(game == null){
            throw new ResponseException(400, "Error: bad request -- invalid game, does not exist");
        }
        String username = authAccess.getAuth(authToken).username();
        switch (joinGameRequest.playerColor().toUpperCase()) {
            case "WHITE" -> {
                if(game != null && (game.whiteUsername() == null 
                || (game.whiteUsername() == null ? username == null : game.whiteUsername().equals(username)))){
                    gameAccess.updateGame(joinGameRequest.gameID(), username, null);
                }
                else{
                    throw new ResponseException(403, "Error: already taken");
                }
            }
            case "BLACK" -> {
                if(game != null && (game.blackUsername() == null 
                || (game.blackUsername() == null ? username == null : game.blackUsername().equals(username)))){
                    gameAccess.updateGame(joinGameRequest.gameID(), null, username);
                }
                else{
                    throw new ResponseException(403, "Error: already taken");
                }
            }
            default -> {
                throw new ResponseException(400, "Error: bad request");
            }
        }
    }

    public Result.Register register(Request.Register registerRequest) throws ResponseException{
        if(registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null){
            throw new ResponseException(400, "Error: bad request");
        }
        String username = registerRequest.username();
        UserData user = new UserData(username, registerRequest.password(), registerRequest.email());
        userAccess.addUserData(user);
        String token = generateToken();
        authAccess.addAuthData(new AuthData(token, username));
        return new Result.Register(username, token);
    }

    public Result.Login login(Request.Login loginRequest) throws ResponseException{
        UserData user = userAccess.getUser(loginRequest.username());
        String password = loginRequest.password();
        if(authAccess.verifyUser(user.password(), password)){
            String token = generateToken();
            authAccess.addAuthData(new AuthData(token, user.username()));
            return new Result.Login(user.username(), token);
        }
        throw new ResponseException(401, "Error: unauthorized");

    }

    public void logout(Request.Logout logoutRequest) throws ResponseException{
        AuthData authData = authAccess.getAuth(logoutRequest.authToken());
        authAccess.removeAuthData(authData); 
    }

    public void updateGame(String authToken, Request.UpdateGame updateRequest) throws ResponseException{
        checkAuth(authToken); 
        gameAccess.updateGame(updateRequest.gameID(), updateRequest.game());
    }

    public void removeGameUser(String authToken, int gameID, String playerColor) throws ResponseException{
        checkAuth(authToken);
        gameAccess.removeUser(gameID, playerColor);
    }

    public void deleteGame(String authToken, Request.DeleteGame deleteRequest) throws ResponseException{
        checkAuth(authToken);
        gameAccess.deleteGame(deleteRequest.gameID());
    }

    public GameData getData(String authToken, int gameID) throws ResponseException{
        checkAuth(authToken);
        GameData data = gameAccess.getGame(gameID); 
        if(data == null){
            throw new ResponseException(401, "Error: bad request -- invalid game");
        }
        return data;
    }

    // Returns the username from an authToken
    public String getUsername(String authToken) throws ResponseException{
        return authAccess.getAuth(authToken).username();
    }

    // update a game from the websocket
    public void updateGame(String authToken, int gameID, ChessGame game) throws ResponseException{
        checkAuth(authToken); 
        gameAccess.updateGame(gameID, game);
    }

    private static String generateToken(){
        return UUID.randomUUID().toString();
    }

    private boolean checkAuth(String authToken) throws ResponseException{
        return (authAccess.getAuth(authToken) != null);
    }

    


}
