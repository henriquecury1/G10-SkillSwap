import { Star } from 'lucide-react'

interface StarRatingProps {
  value: number
  max?: number
  size?: number
  interactive?: boolean
  onChange?: (val: number) => void
}

export default function StarRating({ value, max = 5, size = 16, interactive, onChange }: StarRatingProps) {
  return (
    <div className="flex gap-0.5">
      {Array.from({ length: max }, (_, i) => {
        const filled = i < Math.round(value)
        return (
          <button
            key={i}
            type="button"
            disabled={!interactive}
            onClick={() => onChange?.(i + 1)}
            className={`${interactive ? 'cursor-pointer hover:scale-110 transition-transform' : 'cursor-default'}`}
          >
            <Star
              size={size}
              className={filled ? 'text-warning fill-warning' : 'text-gray-300'}
            />
          </button>
        )
      })}
    </div>
  )
}
