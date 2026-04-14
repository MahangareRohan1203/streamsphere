import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useUploadVideoMutation } from '../features/videos/videoApi';
import Navbar from '../components/Navbar';
import { Upload as UploadIcon, AlertCircle, CheckCircle2 } from 'lucide-react';

const Upload = () => {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [file, setFile] = useState<File | null>(null);
  const [uploadVideo, { isLoading, isSuccess, isError, error }] = useUploadVideoMutation();
  const navigate = useNavigate();

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files) {
      setFile(e.target.files[0]);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!file) return;

    const formData = new FormData();
    formData.append('title', title);
    formData.append('description', description);
    formData.append('file', file);

    try {
      await uploadVideo(formData).unwrap();
      // Optionally reset form or wait for success state
    } catch (err) {
      console.error('Failed to upload video:', err);
    }
  };

  return (
    <>
      <Navbar />
      <div className="max-w-2xl mx-auto mt-16 p-8 bg-surface rounded-lg shadow-2xl border border-gray-800">
        <div className="flex items-center gap-3 mb-8">
          <UploadIcon className="text-primary" size={32} />
          <h2 className="text-3xl font-bold">Upload Video</h2>
        </div>

        {isSuccess && (
          <div className="mb-6 p-4 bg-green-900 bg-opacity-30 border border-green-500 rounded-md flex items-center gap-3 text-green-400">
            <CheckCircle2 size={20} />
            <p>Video uploaded successfully! It is now being processed.</p>
          </div>
        )}

        {isError && (
          <div className="mb-6 p-4 bg-red-900 bg-opacity-30 border border-red-500 rounded-md flex items-center gap-3 text-red-400">
            <AlertCircle size={20} />
            <p>Upload failed. Please check the file size and your connection.</p>
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-6">
          <div>
            <label className="block text-sm font-medium text-gray-400 mb-2">Video Title</label>
            <input
              type="text"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              className="w-full p-3 bg-gray-800 border border-gray-700 rounded-md focus:outline-none focus:ring-2 focus:ring-primary"
              placeholder="e.g. My Amazing Travel Vlog"
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-400 mb-2">Description</label>
            <textarea
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              className="w-full p-3 bg-gray-800 border border-gray-700 rounded-md focus:outline-none focus:ring-2 focus:ring-primary h-32"
              placeholder="Tell viewers about your video..."
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-400 mb-2">Select Video File</label>
            <div className="mt-1 flex justify-center px-6 pt-5 pb-6 border-2 border-gray-700 border-dashed rounded-md hover:border-primary transition duration-200">
              <div className="space-y-1 text-center">
                <UploadIcon className="mx-auto h-12 w-12 text-gray-400" />
                <div className="flex text-sm text-gray-400">
                  <label htmlFor="file-upload" className="relative cursor-pointer bg-transparent rounded-md font-medium text-primary hover:text-red-500 focus-within:outline-none">
                    <span>Upload a file</span>
                    <input id="file-upload" name="file-upload" type="file" className="sr-only" accept="video/*" onChange={handleFileChange} required />
                  </label>
                  <p className="pl-1">or drag and drop</p>
                </div>
                <p className="text-xs text-gray-500">MP4, MKV up to 50MB</p>
                {file && <p className="text-sm text-white font-bold mt-2">Selected: {file.name}</p>}
              </div>
            </div>
          </div>

          <button
            type="submit"
            disabled={isLoading}
            className="w-full py-4 bg-primary text-white font-bold rounded-md hover:bg-red-700 transition duration-200 disabled:opacity-50 flex justify-center items-center gap-2"
          >
            {isLoading ? 'Uploading...' : 'Start Upload'}
          </button>
        </form>
      </div>
    </>
  );
};

export default Upload;
