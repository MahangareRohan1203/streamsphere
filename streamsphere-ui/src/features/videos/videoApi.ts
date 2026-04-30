import { api } from '../../services/api';

export type VideoResolution = {
  resolution: string;
  videoUrl: string;
};

export type VideoResponse = {
  id: number;
  title: string;
  description: string;
  originalFileName: string;
  rawVideoUrl: string;
  status: string;
  createdAt: string;
  resolutions: VideoResolution[];
};

export type PaginatedResponse<T> = {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
};

export type VideoQueryParams = {
  status?: string;
  page?: number;
  size?: number;
  sort?: string;
};

export const videoApi = api.injectEndpoints({
  endpoints: (builder) => ({
    getVideos: builder.query<PaginatedResponse<VideoResponse>, VideoQueryParams | void>({
      query: (params) => ({
        url: '/api/videos',
        params: params || {},
      }),
      providesTags: ['Video'],
    }),
    getVideoById: builder.query<VideoResponse, number>({
      query: (id) => `/api/videos/${id}`,
      providesTags: (_result, _error, id) => [{ type: 'Video', id }],
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
