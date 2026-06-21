export interface ProfileDimension {
  code: string
  name: string
  value: string
  confidence?: number
  source?: string
  displayOnly?: boolean
}

export interface LearningProfile {
  id?: number
  studentId?: number
  profileVersion?: number
  profileSummary?: string
  completenessScore?: number
  lastGeneratedAt?: string
  dimensions?: ProfileDimension[]
  displayOnly?: boolean
}

export interface ProfileMessage {
  id?: number
  role: 'student' | 'assistant' | string
  content: string
  createdAt?: string
  displayOnly?: boolean
}

export interface ProfileSession {
  id?: number
  studentId?: number
  sessionTitle?: string
  confirmStatus?: string
  confidenceScore?: number
  confirmedProfileId?: number
  draftProfile?: string
  dimensions?: ProfileDimension[]
  messages?: ProfileMessage[]
  createdAt?: string
  displayOnly?: boolean
}

export interface ProfileSessionCreateRequest {
  sessionTitle?: string
}

export interface ProfileSessionMessageRequest {
  content: string
}

export interface ProfileExtractRequest {
  profileSummary?: string
  confidenceScore?: number
  dimensions?: ProfileDimension[]
}

export interface ProfileConfirmRequest {
  sessionId: number
}
