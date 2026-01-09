import { useState } from 'react'
import { motion } from 'framer-motion'

const slides = [
  {
    title: 'Connect your bank accounts',
    desc: "Link your bank accounts or import CSVs to track spending in one place.",
    art: '💳'
  },
  {
    title: 'Your privacy is our priority',
    desc: "We use end-to-end encryption and zero-knowledge architecture. Your data stays secure and private. 🔒",
    art: '🔐'
  },
  {
    title: 'Set your financial goals',
    desc: "Saving for a rainy day, a new car, or a vacation? We'll help plan it.",
    art: '🎯'
  },
  {
    title: 'Explore our features',
    desc: 'Budgeting tools, investment tracking, and explainable AI insights.',
    art: '📊'
  }
]

export default function Onboarding() {
  const [step, setStep] = useState(0)
  const s = slides[step]

  return (
    <div className="max-w-3xl mx-auto">
      <div className="card p-8 md:p-10 grid md:grid-cols-2 gap-8 items-center">
        <motion.div
          key={step}
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          className="aspect-square rounded-2xl bg-gradient-to-tr from-brand-500/20 to-white/5 flex items-center justify-center text-7xl"
        >
          <span aria-hidden>{s.art}</span>
        </motion.div>

        <div>
          <div className="flex items-center justify-between mb-6">
            <div className="text-sm text-slate-400">Step {step + 1} of {slides.length}</div>
            <button className="text-slate-400 hover:text-white/80" onClick={()=>setStep(slides.length-1)}>Skip</button>
          </div>
          <h2 className="text-3xl font-semibold mb-3">{s.title}</h2>
          <p className="text-slate-300 mb-6">{s.desc}</p>

          <div className="flex items-center gap-2 mb-6">
            {slides.map((_, i) => (
              <div key={i} className={`h-1 rounded-full ${i===step? 'w-12 bg-brand-500':'w-6 bg-white/10'}`} />
            ))}
          </div>

          <div className="flex gap-3">
            <button
              className="px-4 py-2 bg-white/10 rounded"
              disabled={step===0}
              onClick={()=>setStep(v=>Math.max(0, v-1))}
            >Back</button>
            <button
              className="px-4 py-2 bg-brand-500 rounded"
              onClick={()=>setStep(v=>Math.min(slides.length-1, v+1))}
            >{step===slides.length-1? 'Finish' : 'Next'}</button>
          </div>
        </div>
      </div>
    </div>
  )
}
