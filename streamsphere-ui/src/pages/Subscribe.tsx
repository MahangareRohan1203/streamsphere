import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useSubscribeMutation } from '../features/auth/authApi';
import { useSelector, useDispatch } from 'react-redux';
import type { RootState } from '../app/store';
import { setUserInfo } from '../features/auth/authSlice';
import Navbar from '../components/Navbar';
import { CheckCircle2 } from 'lucide-react';

const plans = [
  {
    name: 'PREMIUM',
    price: '$9.99/mo',
    features: ['1080p Resolution', 'Ad-free experience', 'Download to watch offline'],
  },
  {
    name: 'GOLD',
    price: '$14.99/mo',
    features: ['4K Resolution', 'Ad-free experience', 'Download to watch offline', '4 Screens at once'],
  }
];

const Subscribe = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const { userId, currentTier, role } = useSelector((state: RootState) => state.auth);
  const [subscribe, { isLoading }] = useSubscribeMutation();
  const [errorMsg, setErrorMsg] = useState('');

  const handleSubscribe = async (tier: string) => {
    if (!userId) {
      setErrorMsg('User ID not found. Please log in again.');
      return;
    }
    
    try {
      const response = await subscribe({ userId, tier }).unwrap();
      dispatch(setUserInfo({ userId, currentTier: response.tier, role: role as string }));
      navigate('/home');
    } catch (err: any) {
      setErrorMsg(err?.data?.message || 'Subscription failed');
    }
  };

  return (
    <>
      <Navbar />
      <div className="min-h-[80vh] flex flex-col items-center justify-center p-8 max-w-5xl mx-auto">
        <h1 className="text-4xl font-extrabold mb-2">Upgrade your plan</h1>
        <p className="text-gray-400 mb-10 text-lg">Current Plan: <span className="text-primary font-bold">{currentTier || 'FREE'}</span></p>

        {errorMsg && <p className="text-red-500 mb-4">{errorMsg}</p>}

        <div className="grid grid-cols-1 md:grid-cols-2 gap-8 w-full">
          {plans.map((plan) => (
            <div key={plan.name} className="bg-surface border border-gray-800 rounded-xl p-8 flex flex-col hover:border-primary transition">
              <h2 className="text-2xl font-bold text-white mb-2">{plan.name}</h2>
              <div className="text-3xl font-extrabold mb-6 text-primary">{plan.price}</div>
              <ul className="flex-1 space-y-4 mb-8">
                {plan.features.map((feature, i) => (
                  <li key={i} className="flex items-center gap-3 text-gray-300">
                    <CheckCircle2 className="text-green-500 w-5 h-5" />
                    <span>{feature}</span>
                  </li>
                ))}
              </ul>
              <button
                onClick={() => handleSubscribe(plan.name)}
                disabled={isLoading || currentTier === plan.name}
                className="w-full bg-primary hover:bg-red-700 text-white font-bold py-3 px-4 rounded transition disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {isLoading ? 'Processing...' : (currentTier === plan.name ? 'Current Plan' : 'Subscribe')}
              </button>
            </div>
          ))}
        </div>
      </div>
    </>
  );
};

export default Subscribe;
