package com.grpcflix.aggregator.service;

import com.giovanacgois.grpcflix.common.Genre;
import com.giovanacgois.grpcflix.movie.MovieSearchRequest;
import com.giovanacgois.grpcflix.movie.MovieSearchResponse;
import com.giovanacgois.grpcflix.movie.MovieServiceGrpc;
import com.giovanacgois.grpcflix.user.UserGenreUpdateRequest;
import com.giovanacgois.grpcflix.user.UserResponse;
import com.giovanacgois.grpcflix.user.UserSearchRequest;
import com.giovanacgois.grpcflix.user.UserServiceGrpc;
import com.grpcflix.aggregator.dto.RecommendedMovie;
import com.grpcflix.aggregator.dto.UserGenre;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserMovieService {

    // Baseado nisso, o Spring faz a criação do canal
    @GrpcClient("user-service") // Mesmo nome definido no arquivo de propriedades
    private UserServiceGrpc.UserServiceBlockingStub userStub;

    // Baseado nisso, o Spring faz a criação do canal
    @GrpcClient("movie-service") // Mesmo nome definido no arquivo de propriedades
    private MovieServiceGrpc.MovieServiceBlockingStub movieStub;

    public List<RecommendedMovie> getUserMovieSuggestions(String loginId) {
        // Primeiramente, com o loginID, vamos procurar o usuário para saber seu gênero preferido.
        UserSearchRequest userSearchRequest = UserSearchRequest.newBuilder().setLoginId(loginId).build();
        UserResponse userResponse = this.userStub.getUserGenre(userSearchRequest);

        // Agora, vamos encontrar a lista de filmes recomendados para esse gênero e retorná-la.
        MovieSearchRequest movieSearchRequest = MovieSearchRequest.newBuilder().setGenre(userResponse.getGenre()).build();
        MovieSearchResponse movieSearchResponse = this.movieStub.getMovies(movieSearchRequest);
        return movieSearchResponse.getMovieList()
                .stream().map(movieDto ->
                        new RecommendedMovie(movieDto.getTitle(), movieDto.getYear(), movieDto.getRating()))
                .collect(Collectors.toList());
    }

    public void setUserGenre(UserGenre userGenre) {
        UserGenreUpdateRequest userGenreUpdateRequest = UserGenreUpdateRequest.newBuilder()
                .setLoginId(userGenre.getLoginId())
                .setGenre(Genre.valueOf(userGenre.getGenre().toUpperCase()))
                .build();

        UserResponse userResponse = this.userStub.updateUserGenre(userGenreUpdateRequest);
    }

}
