# Spring AI RAG Assistant

A production-style **Retrieval-Augmented Generation (RAG)** system built with **Spring Boot and Spring AI**, designed to answer questions over technical documentation with grounded, traceable responses.

---

## 🚀 Overview

This project demonstrates how to build a **Java-native AI application** using Spring AI, focusing on:

- Document ingestion and preprocessing
- Vector-based semantic search
- Context-aware LLM responses
- Grounded answers with citations
- Clean, testable architecture

The goal is not just to “wrap an LLM”, but to implement a **reliable, inspectable AI system** suitable for enterprise use.

---

## 🧠 What is RAG?

Retrieval-Augmented Generation (RAG) improves LLM responses by:

1. Retrieving relevant documents from a knowledge base
2. Injecting them into the prompt context
3. Generating answers grounded in real data

This avoids hallucinations and enables **traceable answers**.

---

## 🏗️ Architecture

```text
           ┌───────────────┐
           │   Documents   │
           └──────┬────────┘
                  │
          (Ingestion + Chunking)
                  │
                  ▼
        ┌────────────────────┐
        │  Vector Store      │
        │ (pgvector / etc.)  │
        └─────────┬──────────┘
                  │
          (Similarity Search)
                  │
                  ▼
        ┌────────────────────┐
        │   Retrieval Layer  │
        └─────────┬──────────┘
                  │
          (Prompt Composition)
                  │
                  ▼
        ┌────────────────────┐
        │     LLM (Chat)     │
        └─────────┬──────────┘
                  │
                  ▼
        ┌────────────────────┐
        │   REST / Streaming │
        └────────────────────┘
