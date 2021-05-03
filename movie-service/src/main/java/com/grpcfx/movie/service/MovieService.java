package com.grpcfx.movie.service;

import com.giovanacgois.grpcflix.movie.MovieDto;
import com.giovanacgois.grpcflix.movie.MovieSearchRequest;
import com.giovanacgois.grpcflix.movie.MovieSearchResponse;
import com.giovanacgois.grpcflix.movie.MovieServiceGrpc;
import com.grpcfx.movie.repository.MovieRepository;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@GrpcService
public class MovieService extends MovieServiceGrpc.MovieServiceImplBase {

    @Autowired
    private MovieRepository movieRepository;

    @Override
    public void getMovies(MovieSearchRequest request, StreamObserver<MovieSearchResponse> responseObserver) {

        // Obtém e prepara os dados desejados
        List<MovieDto> movieDtoList = this.movieRepository.getMovieByGenreOrderByYearDesc(request.getGenre().toString())
                .stream()
                .map(movie ->
                        MovieDto.newBuilder()
                                .setTitle(movie.getTitle())
                                .setYear(movie.getYear())
                                .setRating(movie.getRating())
                                .build())
                .collect(Collectors.toList());

        // Devolve a resposta da request
        responseObserver.onNext(MovieSearchResponse.newBuilder().addAllMovie(movieDtoList).build());
        responseObserver.onCompleted();

    }
}
