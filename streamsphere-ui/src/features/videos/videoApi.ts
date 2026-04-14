import { api } from '../../services/api';

export interface VideoResolution {
  resolution: string;
  videoUrl: string;
}

export interface VideoResponse {
  id: number;
  title: string;
  description: string;
  originalFileName: string;
  rawVideoUrl: string;
  status: string;
  createdAt: string;
  resolutions: VideoResolution[];
}

export const videoApi = api.injectEndpoints({
  endpoints: (builder) => ({
    getVideos: builder.query<VideoResponse[], void>({
      query: () => '/api/videos', // Note: We need to implement this in the backend later
      providesTags: ['Video'],
    }),
    getVideoById: builder.query<VideoResponse, number>({
      query: (id) => `/api/videos/${id}`,
      providesTags: (result, error, id) => [{ type: 'Video', id }],
    }),
    uploadVideo: builder.mutation<VideoResponse, FormData>({
      query: (formData) => ({
        url: '/api/videos/upload',
        method: 'POST',
        body: formData,
      }),
      invalidatesTags: ['Video'],
    }),
  }),
});

export const { 
  useGetVideosQuery, 
  useGetVideoByIdQuery, 
  useUploadVideoMutation 
} = videoApi;
